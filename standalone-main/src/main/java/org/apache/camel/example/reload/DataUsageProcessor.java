package org.apache.camel.example.reload;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class DataUsageProcessor implements Processor {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataUsageProcessor.class);
    private static final String DATA_USAGE_BYTES_PER_TRANSACTION = "dataUsage.bytesPerTransaction";
    static final String DATA_USAGE_FROM = "dataUsage.from";
    static final String DATA_USAGE_OPERATION = "dataUsage";
    static final String SEND_TO_JIRA = "sendToJira";
    private int bytesPerTransaction;

    @Override
    public void process(Exchange exchange) throws Exception {
        String inBody = exchange.getIn().getBody(String.class);
        bytesPerTransaction = exchange.getProperty(DATA_USAGE_BYTES_PER_TRANSACTION, bytesPerTransaction, Integer.class);
        String[] terminalsAndTransactions = inBody.split(CsvAggregationStrategy.DELIMITER);
        LOGGER.info("Bytes Per Transaction = {}", bytesPerTransaction);
        if (terminalsAndTransactions.length != 3) {
            LOGGER.error("Received in message MUST contain exactly 3 elements but contains {}", terminalsAndTransactions.length);
            return;
        }
        String terminals = terminalsAndTransactions[0];
        String dataUsage = terminalsAndTransactions[1];
        String transactions = terminalsAndTransactions[2];
        LOGGER.info("Received terminals message with size {}", terminals.length());
        LOGGER.info("Received transactions message with size {}", transactions.length());
        LOGGER.info("Received terminal data usage message with size {}", dataUsage.length());
        CSVParser csvTerminals = CSVParser.parse(terminals, ExpectedTerminalDataUsage.TERM_CSV_FORMAT);
        CSVParser csvDataUsage = CSVParser.parse(dataUsage, ExpectedTerminalDataUsage.TERM_CSV_FORMAT);
        Set<String> terminalsSet = ExpectedTerminalDataUsage.getTerminalsFromNitra(csvTerminals);
        Map<String, Integer> terminalDataUsage = ExpectedTerminalDataUsage.getTerminalsDataUsage(csvDataUsage);
        LOGGER.info("Terminals count = {}", terminalsSet.size());
        LOGGER.info("Terminals with data usage count = {}", terminalDataUsage.size());
        CSVParser csvTransactions = CSVParser.parse(transactions, ExpectedTerminalDataUsage.TRX_CSV_FORMAT);
        Map<String, Integer> expectedDataUsageMap =
                ExpectedTerminalDataUsage.getExpectedTerminalDataUsage(terminalsSet, csvTransactions,
                        bytesPerTransaction, ExpectedTerminalDataUsage.FROM);
        StringBuilder body = new StringBuilder();
        int exceeded = 0;
        for (String terminalId : expectedDataUsageMap.keySet()) {
            int expectedDataUsage = expectedDataUsageMap.get(terminalId);
            int realDataUsage = terminalDataUsage.get(terminalId);
            LOGGER.info("TerminalID {}: Expected/Real Usage  {}/{} MB", terminalId, expectedDataUsage, realDataUsage);
            if (realDataUsage > expectedDataUsage) {
                exceeded++;
                body.append(String.format("%s:expected/real %d/%d MB,", terminalId, expectedDataUsage, realDataUsage));
            }
        }
        exchange.setProperty(MaxGapProcessor.OPERATION_NAME, DATA_USAGE_OPERATION);
        if (exceeded > 0) {
            exchange.getIn().setHeader(SEND_TO_JIRA, exceeded);
        } else {
            LOGGER.info("No terminals with exceeded usage");
        }
        exchange.getIn().setBody(body.toString());
    }

    public int getBytesPerTransaction() {
        return bytesPerTransaction;
    }

    public void setBytesPerTransaction(int bytesPerTransaction) {
        this.bytesPerTransaction = bytesPerTransaction;
    }
}
