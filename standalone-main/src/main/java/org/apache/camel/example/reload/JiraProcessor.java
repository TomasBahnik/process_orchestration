package org.apache.camel.example.reload;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraProcessor implements Processor {

    private final static Logger LOGGER = LoggerFactory.getLogger(JiraProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        String inBody = exchange.getIn().getBody(String.class);
        LOGGER.info("JSON {}", inBody);
        exchange.getIn().setBody(inBody);
    }
}
