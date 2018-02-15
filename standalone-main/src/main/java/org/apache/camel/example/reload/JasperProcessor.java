package org.apache.camel.example.reload;

import com.jasperwireless.api.ws.schema.TerminalPortType;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.BusFactory;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecUsernameToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class JasperProcessor implements Processor {
    private final static Logger LOGGER = LoggerFactory.getLogger(JasperProcessor.class);

    private String jasperWirelessAddress;
    private TerminalPortType proxy;

    public JasperProcessor(String jasperWirelessAddress) {
        this.jasperWirelessAddress = jasperWirelessAddress;
        proxy = getProxy();
    }

    //com.aevi.nitra.core.connector.jasper.JasperConnector#getJasperService

    @Override
    public void process(Exchange exchange) throws Exception {

    }

    private TerminalPortType getProxy() {
        // Here we use JaxWs front end to create the proxy
        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
        ClientFactoryBean clientBean = proxyFactory.getClientFactoryBean();
        clientBean.setAddress(jasperWirelessAddress);
        clientBean.setServiceClass(TerminalPortType.class);
        clientBean.setBus(BusFactory.getDefaultBus());
        return (TerminalPortType) proxyFactory.create();
    }

    private TerminalPortType getJasperService()  {

        // padalo to, ak to dotahujem online z
        // http://kpn.jasperwireless.com/ws/schema/Terminal.wsdl
        // tak WSDL berieme z resource, URL sluzby je stale to iste ako pre UAT
        // aj pre PROD, takze nam to nevadi
        URL url = this.getClass().getResource("/wsdl/jasper/Terminal_1.wsdl");
        QName qname = new QName("http://api.jasperwireless.com/ws/schema", "TerminalService");
        Service service = Service.create(url, qname);
        TerminalPortType port = service.getPort(TerminalPortType.class);

        BindingProvider bindingProvider = (BindingProvider) port;

        List<Handler> handlerChain = new ArrayList<>();
        handlerChain.add(new SOAPHandler<SOAPMessageContext>() {
            @Override
            public boolean handleMessage(SOAPMessageContext context) {
                Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

                if (outboundProperty) {
                    try {
                        SOAPMessage message = context.getMessage();
                        SOAPPart soapPart = message.getSOAPPart();
                        WSSecUsernameToken builder = new WSSecUsernameToken();
                        WSSecHeader header = new WSSecHeader();
                        header.insertSecurityHeader(soapPart);
                        builder.setPasswordType(WSConstants.PASSWORD_TEXT);
                        builder.setUserInfo("jasper.username","jasper.password");
                        builder.build(soapPart, header);

                        message.saveChanges();
                    } catch (WSSecurityException | SOAPException e) {
                        LOGGER.error("Jasper add security header error.", e);
                        e.printStackTrace();
                    }
                }
                return true;
            }

            @Override
            public boolean handleFault(SOAPMessageContext context) {
                return true;
            }

            @Override
            public void close(MessageContext context) {
            }

            @Override
            public Set<QName> getHeaders() {
                return Collections.emptySet();
            }
        });

        bindingProvider.getBinding().setHandlerChain(handlerChain);
        return port;
    }
}
