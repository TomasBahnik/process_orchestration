package org.apache.camel.example.reload;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CsvProcessor implements Processor {

    private static final int REQUEST_TIME = 15;
    private static final int REQUEST_TYPE = 2;
    private final static Logger LOGGER = LoggerFactory.getLogger(CsvProcessor.class);
    private int limit;

    @Override
    public void process(Exchange exchange) throws Exception {
        String inBody = exchange.getIn().getBody(String.class);
        int length = inBody.length();
        String[] terminalsAndTransactions = inBody.split(CsvAggregationStrategy.DELIMITER);
        if (terminalsAndTransactions.length != 2) {
            LOGGER.error("Received in message MUST contain exactly 2 elements but contains {}", terminalsAndTransactions.length);
            return;
        }
        String terminals = terminalsAndTransactions[0];
        String transactions = terminalsAndTransactions[1];
        LOGGER.info("Received terminals message with size {}", terminals.length());
        LOGGER.info("Received transactions message with size {}", transactions.length());
        CSVParser csvTransactions = CSVParser.parse(transactions, ExpectedTerminalDataUsage.TRX_CSV_FORMAT);
        CSVParser csvTerminals = CSVParser.parse(terminals, ExpectedTerminalDataUsage.TERM_CSV_FORMAT);
        //Set<String> terminalsSet = ExpectedTerminalDataUsage.getTerminalsFromNitra(csvTerminals);
        int count = 10;
        Map<String, Integer> expectedDataUsage =
                ExpectedTerminalDataUsage.getExpectedTerminalDataUsage(csvTerminals, csvTransactions,
                        ExpectedTerminalDataUsage.BYTES_PER_TRANSACTION, ExpectedTerminalDataUsage.FROM);
        for (String terminalId : expectedDataUsage.keySet()) {
            LOGGER.info("TerminalID {}: Usage {} MB", terminalId, expectedDataUsage.get(terminalId));
        }
        exchange.getIn().setBody(count);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
