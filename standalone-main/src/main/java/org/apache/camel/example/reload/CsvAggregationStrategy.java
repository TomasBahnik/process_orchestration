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
        if (oldExchange == null) {
            LOGGER.debug("new exchange {}, new exchange aggregated size {}, message count {}", newExchange,
                    newExchange, newExchange.getProperty(Exchange.AGGREGATED_SIZE));
            return newExchange;
        }
        String input = newExchange.getIn().getBody(String.class);
        int aggregatedSize = Integer.valueOf(oldExchange.getProperty(Exchange.AGGREGATED_SIZE).toString());
        LOGGER.debug("newExchange input {}, CamelAggregatedSize {}", input, aggregatedSize );

/*
        if (aggregatedSize > 1 || input.contains(STOP))
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
