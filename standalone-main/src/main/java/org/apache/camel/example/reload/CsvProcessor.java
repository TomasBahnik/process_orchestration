package org.apache.camel.example.reload;

import static org.apache.camel.example.reload.TrendAnomaly.REQUEST_DATE_PATTERN;
import static org.apache.camel.example.reload.TrendAnomaly.TRANSACTION_TIMEZONE;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class CsvProcessor implements Processor {

    private static final int REQUEST_TIME = 15;
    private static final int REQUEST_TYPE = 2;
    private final static Logger LOGGER = LoggerFactory.getLogger(CsvProcessor.class);
    static final int MAX_GAP_IN_SECONDS = 3600;
    static final LocalTime FROM = LocalTime.of(8, 0);
    static final LocalTime TO = LocalTime.of(16, 0);
    private int limit;
    private int maxGapInSeconds;
    private String date;

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
/*
        Map<String, Integer> expectedDataUsage =
                ExpectedTerminalDataUsage.getExpectedTerminalDataUsage(terminalsSet, csvTransactions,
                        ExpectedTerminalDataUsage.BYTES_PER_TRANSACTION, ExpectedTerminalDataUsage.FROM);
        for (String terminalId : expectedDataUsage.keySet()) {
            LOGGER.info("TerminalID {}: Usage {} MB", terminalId, expectedDataUsage.get(terminalId));
        }
*/
        LocalDate transactionDate = LocalDate
                .parse(date, DateTimeFormatter.ofPattern(REQUEST_DATE_PATTERN).withZone(
                        ZoneId.of(TRANSACTION_TIMEZONE)));

        Set<String> terminalsExceedingGap = TrendAnomaly.getTerminalsExceedingGap(terminalsSet, maxGapInSeconds, transactionDate, FROM, TO, csvTransactions);
        exchange.getIn().setBody(terminalsExceedingGap.toString());
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getMaxGapInSeconds() {
        return maxGapInSeconds;
    }

    public void setMaxGapInSeconds(int maxGapInSeconds) {
        this.maxGapInSeconds = maxGapInSeconds;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
