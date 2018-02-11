package org.apache.camel.example.reload;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessoryOrderProcessor implements Processor {

    private final static Logger LOGGER = LoggerFactory.getLogger(AccessoryOrderProcessor.class);
    private int paperPerTrx;

    @Override
    public void process(Exchange exchange) throws Exception {
        Integer inBody = exchange.getIn().getBody(Integer.class);
        LOGGER.info("Received in message with {} trx, paper per trx {}", inBody, paperPerTrx);
        LOGGER.info("Paper consumed {}", inBody * paperPerTrx);
    }

    public int getPaperPerTrx() {
        return paperPerTrx;
    }

    public void setPaperPerTrx(int paperPerTrx) {
        this.paperPerTrx = paperPerTrx;
    }
}
