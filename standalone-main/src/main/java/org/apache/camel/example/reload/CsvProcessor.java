package org.apache.camel.example.reload;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

public class CsvProcessor implements Processor {

    private static final int REQUEST_TIME = 15;
    private static final int REQUEST_TYPE = 2;
    private final static Logger LOGGER = LoggerFactory.getLogger(CsvProcessor.class);
    static final int MAX_GAP_IN_SECONDS = 3600;
    static final LocalDate DATE = LocalDate.of(2017, 6, 5);
    static final LocalTime FROM = LocalTime.of(9, 0);
    static final LocalTime TO = LocalTime.of(13, 0);
    private int limit;

    @Override
    public void process(Exchange exchange) throws Exception {
        String inBody = exchange.getIn().getBody(String.class);
        String[] terminalsAndTransactions = inBody.split(CsvAggregationStrategy.DELIMITER);
        if (terminalsAndTransactions.length != 2) {
            LOGGER.error("Received in message MUST contain exactly 2 elements but contains {}", terminalsAndTransactions.length);
            return;
        }
        String terminals = terminalsAndTransactions[0];
        String transactions = terminalsAndTransactions[1];
        LOGGER.info("Received terminals message with size {}", terminals.length());
        LOGGER.info("Received transactions message with size {}", transactions.length());
        CSVParser csvTerminals = CSVParser.parse(terminals, ExpectedTerminalDataUsage.TERM_CSV_FORMAT);
        Set<String> terminalsSet = ExpectedTerminalDataUsage.getTerminalsFromNitra(csvTerminals);
        LOGGER.info("Terminals count = {}", terminalsSet.size());
        CSVParser csvTransactions = CSVParser.parse(transactions, ExpectedTerminalDataUsage.TRX_CSV_FORMAT);
        int count = 10;
        Map<String, Integer> expectedDataUsage =
                ExpectedTerminalDataUsage.getExpectedTerminalDataUsage(terminalsSet, csvTransactions,
                        ExpectedTerminalDataUsage.BYTES_PER_TRANSACTION, ExpectedTerminalDataUsage.FROM);
        for (String terminalId : expectedDataUsage.keySet()) {
            LOGGER.info("TerminalID {}: Usage {} MB", terminalId, expectedDataUsage.get(terminalId));
        }
        Set<String> terminalsExceedingGap = TrendAnomaly.getTerminalsExceedingGap(terminalsSet, MAX_GAP_IN_SECONDS, DATE, FROM, TO, csvTransactions);
        exchange.getIn().setBody(terminalsExceedingGap.size());
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
