package org.apache.camel.example.reload;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Spocita pocet predpokldadany objem dat, ktery by mel terminal spotrebovat dle poctu transakci. V potaz se berou pouze terminaly obsazene
 * v souboru s terminaly. Toto by mel byt jeden procesor. Dalsi procesor by mel pro vsechny terminaly vracene timto procesorem zjistit z
 * Jasperu (zatim to muze byt dummy) realnou usage, vyhodnodit, zda je to vice, nez by se melo spotrebovat a dle toho by raisnul issue v
 * JIRA.
 *
 * Mozna by bylo fajn rozdelit na 2 procesory, kdy jeden nacte vsechny terminaly z Nitry (metoda getTerminalsFromNitra()).
 *
 * @author pavel.sindelar
 */
public class ExpectedTerminalDataUsage {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExpectedTerminalDataUsage.class);

    static final String TERMINAL_PATH = "C:\\Users\\moro\\git\\TomasBahnik\\process_orchestration\\standalone-main\\temp\\terminal.csv";
    private static final String TRANSACTION_PATH = "C:\\Users\\moro\\git\\TomasBahnik\\process_orchestration\\standalone-main\\temp\\transactions.csv";

    private static final String REQUEST_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final int REQUEST_TIME_COLUMN = 15;
    private static final int ID_COLUMN = 1;
    private static final int REQUEST_FLAG_COLUMN = 3;

    private static final String TRANSACTION_TIMEZONE = "Europe/Prague";
    private static final String REQUEST_FLAG_VALUE = "Normal";
    static final LocalDateTime FROM = LocalDateTime.of(2017, 10, 1, 0, 0, 0);
    static final int BYTES_PER_TRANSACTION = 2000;

    static final CSVFormat TRX_CSV_FORMAT = CSVFormat.DEFAULT.withDelimiter(';').withQuote('"');
    static final CSVFormat TERM_CSV_FORMAT = CSVFormat.DEFAULT.withDelimiter(',').withQuote('"');

    public static void main(String[] args) throws Exception {
        Map<String, Integer> expectedDataUsage = getExpectedTerminalDataUsage(BYTES_PER_TRANSACTION, FROM);
        for (String terminalId : expectedDataUsage.keySet()) {
            System.out.println(String.format("%s: %s KB", terminalId, expectedDataUsage.get(terminalId)));
        }
    }

    private static Map<String, Integer> getExpectedTerminalDataUsage(int bytesPerTransaction, LocalDateTime from) throws Exception {
        Set<String> terminals = getTerminalsFromNitra(TERMINAL_PATH);
        Map<String, Integer> transactionCounts = getTransactionCount(terminals, from, TRANSACTION_PATH);
        return getExpectedTerminalDataUsage(bytesPerTransaction, transactionCounts);
    }

    static Map<String, Integer> getExpectedTerminalDataUsage(Set<String> terminals, CSVParser csvTransactions,
                                                             int bytesPerTransaction, LocalDateTime from) throws Exception {
        Map<String, Integer> transactionCounts = getTransactionCount(terminals, csvTransactions, from);
        return getExpectedTerminalDataUsage(bytesPerTransaction, transactionCounts);
    }

    private static Map<String, Integer> getExpectedTerminalDataUsage(int bytesPerTransaction, Map<String, Integer> transactionCounts) {
        Map<String, Integer> result = new HashMap<>();
        for (String terminalId : transactionCounts.keySet()) {
            result.put(terminalId, transactionCounts.get(terminalId) * bytesPerTransaction);
        }
        return result;
    }

    private static Set<String> getTerminalsFromNitra(String csvTerminalPath) throws Exception {
        FileInputStream inputStream = new FileInputStream(csvTerminalPath);
        CSVParser csvParser = CSVParser.parse(inputStream, Charset.forName("UTF8"), TERM_CSV_FORMAT);
        return getTerminalsFromNitra(csvParser);
    }

    static Set<String> getTerminalsFromNitra(CSVParser csvTerminals) {
        Set<String> terminals = new HashSet<>();
        int count = 0;
        for (CSVRecord terminal : csvTerminals) {
            count++;
            String terminalId = terminal.get(0);
            terminals.add(terminalId);
        }
        LOGGER.info("Terminals count = {}", count);
        return terminals;
    }

    static Map<String, Integer> getTerminalsDataUsage(CSVParser csvTerminals) {
        Map<String, Integer> result = new HashMap<>();
        int count = 0;
        for (CSVRecord terminal : csvTerminals) {
            count++;
            String terminalId = terminal.get(0);
            Integer dataUsage = Integer.valueOf(terminal.get(1));
            result.put(terminalId, dataUsage);
        }
        LOGGER.info("Terminals with data usage count = {}", count);
        return result;
    }

    private static Map<String, Integer> getTransactionCount(Set<String> terminals, LocalDateTime from,
                                                            String csvTransactionPath) throws Exception {
        FileInputStream inputStream = new FileInputStream(csvTransactionPath);
        CSVParser csvTransactions = CSVParser.parse(inputStream, Charset.forName("UTF8"), TRX_CSV_FORMAT);
        return getTransactionCount(terminals, csvTransactions, from);
    }

    private static Map<String, Integer> getTransactionCount(Set<String> terminals, CSVParser csvTransactions, LocalDateTime from) {
        Map<String, Integer> result = new HashMap<>();
        int count = 0;
        for (CSVRecord transaction : csvTransactions) {
            count++;
            String transactionId = transaction.get(ID_COLUMN);
            String requestDateTimeString = transaction.get(REQUEST_TIME_COLUMN);
            LocalDateTime requestDateTime = LocalDateTime
                    .parse(requestDateTimeString, DateTimeFormatter.ofPattern(REQUEST_TIME_PATTERN).withZone(
                            ZoneId.of(TRANSACTION_TIMEZONE)));
            if (terminals.contains(transactionId) && requestDateTime.isAfter(from)) {
                String requestFlag = transaction.get(REQUEST_FLAG_COLUMN);
                if (requestFlag != null && requestFlag.equals(REQUEST_FLAG_VALUE)) {
                    result.computeIfPresent(transactionId, (key, oldValue) -> ++oldValue);
                    result.putIfAbsent(transactionId, 1);
                }
            }
        }
        LOGGER.info("Transactions count = {}", count);
        return result;
    }
}
