package org.apache.camel.example.reportincident;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class CsvProcessor implements Processor {

    private final static Logger LOGGER = LoggerFactory.getLogger(CsvProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        String inBody = exchange.getIn().getBody(String.class);
        LOGGER.info("Received in message with body length {}", inBody.length());
        CSVParser csvParser = CSVParser.parse(inBody, CSVFormat.DEFAULT);
        for (CSVRecord csvRecord : csvParser) {
            String requestTime = csvRecord.get(15);
            LOGGER.info("Trx Request Time {}", requestTime);
            TemporalAccessor date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Europe/Paris")).parse(requestTime);
            Instant instant = Instant.from(date);
            LOGGER.info("Trx Instant {}", instant);
        }
        exchange.getIn().setBody(inBody);
    }
}
