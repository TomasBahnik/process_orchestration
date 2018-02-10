package org.apache.camel.example.reload;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvProcessor implements Processor {
    private final static Logger LOGGER = LoggerFactory.getLogger(CsvProcessor.class);
    private int limit;

    @Override
    public void process(Exchange exchange) throws Exception {
        String inBody = exchange.getIn().getBody(String.class);
        LOGGER.info("Received in message with body length {} with limit {}", inBody.length(), limit);
        exchange.getIn().setBody(inBody);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
