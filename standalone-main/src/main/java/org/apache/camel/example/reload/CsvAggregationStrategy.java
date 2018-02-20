package org.apache.camel.example.reload;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvAggregationStrategy implements AggregationStrategy {

    private final static Logger LOGGER = LoggerFactory.getLogger(CsvAggregationStrategy.class);
    static final String DELIMITER = "DELIMITER";
    private static final String STOP = "STOP";

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        String input = newExchange.getIn().getBody(String.class);
        LOGGER.info("new input : size = {}", input.length());
        if (oldExchange == null) {
            return newExchange;
        }

/*
        if (input.contains(STOP))
            return oldExchange;
*/

        String terminals = oldExchange.getIn().getBody(String.class);
        String transactions = newExchange.getIn().getBody(String.class);

        String terminalsAndTransactions = (terminals != null ? terminals : "") + DELIMITER +
                (transactions != null ? transactions : "");
        oldExchange.getIn().setBody(terminalsAndTransactions);

        return oldExchange;
    }
}
