package org.apache.camel.example.reload;

import static org.apache.camel.example.reload.ExpectedTerminalDataUsage.TERM_CSV_FORMAT;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author pavel.sindelar
 */
public class TrendAnomaly {

    private static final LocalDate DATE = LocalDate.of(2018, 2, 13);
    private final static Logger LOGGER = LoggerFactory.getLogger(TrendAnomaly.class);

    private static final String TRANSACTION_PATH = "C:\\Users\\moro\\git\\TomasBahnik\\process_orchestration\\standalone-main\\temp\\transactions.csv";

    private static final String REQUEST_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    static final String REQUEST_DATE_PATTERN = "yyyy-MM-dd";

    static final String TRANSACTION_TIMEZONE = "Europe/Prague";

    public static void main(String[] args) throws Exception {
        Set<String> exceedingTerminals = getTerminalsExceedingGap(getTerminalsFromNitra(), MaxGapProcessor.MAX_GAP_IN_SECONDS, DATE, MaxGapProcessor.FROM, MaxGapProcessor.TO,
                TRANSACTION_PATH);
        System.out.println(exceedingTerminals);
    }

    private static Set<String> getTerminalsFromNitra() throws IOException {
        FileInputStream inputStream = new FileInputStream(ExpectedTerminalDataUsage.TERMINAL_PATH);
        CSVParser csvParser = CSVParser.parse(inputStream, Charset.forName("UTF8"), TERM_CSV_FORMAT);
        return ExpectedTerminalDataUsage.getTerminalsFromNitra(csvParser);
    }

    private static Set<String> getTerminalsExceedingGap(Set<String> terminals, int maxGapInSeconds, LocalDate date,
                                                        LocalTime from,
                                                        LocalTime to, String csvTerminalsPath)
            throws Exception {
        FileInputStream inputStream = new FileInputStream(csvTerminalsPath);
        CSVParser csvTransactions = CSVParser.parse(inputStream, Charset.forName("UTF8"), ExpectedTerminalDataUsage.TRX_CSV_FORMAT);
        return getTerminalsExceedingGap(terminals, maxGapInSeconds, date, from, to, csvTransactions);
    }

    static Set<String> getTerminalsExceedingGap(Set<String> terminals, int maxGapInSeconds, LocalDate date,
                                                LocalTime from, LocalTime to, CSVParser csvTransactions) {
        LOGGER.info("Max Gap in Seconds {}, Date {}, From {}, To {}", maxGapInSeconds, date, from, to);
        Set<String> result = new HashSet<>();
        Map<String, LocalTime> earliestTransactions = new HashMap<>();
        for (CSVRecord csvRecord : csvTransactions) {
            String terminalId = csvRecord.get(1);
            String requestFlag = csvRecord.get(3);

            if (requestFlag != null && requestFlag.equals("Normal") & terminals.contains(terminalId) && !result.contains(terminalId)) {
                // transaction of terminal defined in Nitra which is not exceeding gap yet
                String requestDateTimeString = csvRecord.get(15);

                LocalDateTime requestDateTime = LocalDateTime
                        .parse(requestDateTimeString, DateTimeFormatter.ofPattern(REQUEST_TIME_PATTERN).withZone(
                                ZoneId.of(TRANSACTION_TIMEZONE)));

                if (date.equals(LocalDate.from(requestDateTime))) {
                    // transaction was performed on the specified date

                    LocalTime requestTime = requestDateTime.toLocalTime();

                    if (requestTime.isBefore(from) || requestTime.isAfter(to)) {
                        // transaction time is before or after the time of day specified
                        continue;
                    }

                    LocalTime earliestTransaction = earliestTransactions.get(terminalId);

                    if (earliestTransaction == null) {
                        // last transaction for this terminal, compare to the 'to' time
                        earliestTransaction = to;
                    }

                    if (Duration.between(requestTime, earliestTransaction).getSeconds() > maxGapInSeconds) {
                        // gap exceeded
                        result.add(terminalId);
                    }

                    earliestTransactions.put(terminalId, requestTime);
                }
            }
        }

        // find terminals with no transactions or terminals with first transaction time and the 'from' time exceeding max gap
        for (String terminalId : terminals) {
            if (!result.contains(terminalId)) {
                LocalTime earliestTransaction = earliestTransactions.get(terminalId);

                if (earliestTransaction != null) {
                    if (Duration.between(earliestTransaction, from).getSeconds() > maxGapInSeconds) {
                        result.add(terminalId);
                    }
                } else {
                    result.add(terminalId);
                }
            }
        }

        return result;
    }
}
