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
        Integer numOfTrx = exchange.getIn().getBody(Integer.class);
        LOGGER.info("Received in message with {} trx, paper per trx {}", numOfTrx, paperPerTrx);
        System.out.printf("Processor %s received in message with %d trx, paper per trx %d%n", this.getClass().getCanonicalName(),
                numOfTrx, paperPerTrx);
        int paperConsumed = numOfTrx * paperPerTrx;
        LOGGER.info("Paper consumed {}", paperConsumed);
        System.out.printf("Processor %s : Paper consumed %d%n", this.getClass().getCanonicalName(), paperConsumed);
    }

    public int getPaperPerTrx() {
        return paperPerTrx;
    }

    public void setPaperPerTrx(int paperPerTrx) {
        this.paperPerTrx = paperPerTrx;
    }
}
