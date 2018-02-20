package org.apache.camel.example.reload;

import static org.apache.camel.example.reload.DataUsageProcessor.SEND_TO_JIRA;
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
import java.util.Set;

public class MaxGapProcessor implements Processor {

    private final static Logger LOGGER = LoggerFactory.getLogger(MaxGapProcessor.class);
    static final int MAX_GAP_IN_SECONDS = 3600;
    static final LocalTime FROM = LocalTime.of(8, 0);
    static final LocalTime TO = LocalTime.of(16, 0);
    static final String MAX_GAP_PROCESSOR_TRANSACTION_DATE = "maxGapProcessor.transactionDate";
    static final String MAX_GAP_PROCESSOR_MAX_GAP_IN_SECONDS = "maxGapProcessor.maxGapInSeconds";
    static final String OPERATION_NAME = "operationName";
    static final String MAX_GAP_OPERATION = "maxGap";
    private int maxGapInSeconds;
    private String date;

    @Override
    public void process(Exchange exchange) throws Exception {
        String inBody = exchange.getIn().getBody(String.class);
        maxGapInSeconds = exchange.getProperty(MAX_GAP_PROCESSOR_MAX_GAP_IN_SECONDS, maxGapInSeconds, Integer.class);
        date = exchange.getProperty(MAX_GAP_PROCESSOR_TRANSACTION_DATE, date, String.class);
        LocalDate transactionDate = LocalDate
                .parse(date, DateTimeFormatter.ofPattern(REQUEST_DATE_PATTERN).withZone(
                        ZoneId.of(TRANSACTION_TIMEZONE)));
        LOGGER.info("maxGapInSeconds = {}, transactionDate = {}", maxGapInSeconds, transactionDate);
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
        Set<String> terminalsExceedingGap = TrendAnomaly.getTerminalsExceedingGap(terminalsSet, maxGapInSeconds, transactionDate, FROM, TO, csvTransactions);
        if (terminalsExceedingGap.isEmpty()) {
            LOGGER.info("No terminals with exceeding gap");
        } else {
            exchange.getIn().setHeader(SEND_TO_JIRA, terminalsExceedingGap.size());
        }
        exchange.setProperty(MAX_GAP_PROCESSOR_MAX_GAP_IN_SECONDS, maxGapInSeconds);
        exchange.setProperty(MAX_GAP_PROCESSOR_TRANSACTION_DATE, date);
        exchange.setProperty(OPERATION_NAME, MAX_GAP_OPERATION);
        exchange.getIn().setBody(terminalsExceedingGap.toString());
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
