package org.apache.camel.example.reload;

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

    private static final int REQUEST_TIME = 15;
    private static final int REQUEST_TYPE = 2;
    private final static Logger LOGGER = LoggerFactory.getLogger(CsvProcessor.class);
    private int limit;

    @Override
    public void process(Exchange exchange) throws Exception {
        String inBody = exchange.getIn().getBody(String.class);
        int length = inBody.length();
        CSVParser csvParser = CSVParser.parse(inBody, CSVFormat.DEFAULT);
        LOGGER.info("Received in message with size {}", length);
        //System.out.printf("Processor %s received in message with size %d%n", this.getClass().getCanonicalName(), length);
        int count = 0;
        for (CSVRecord csvRecord : csvParser) {
            String requestTime = csvRecord.get(REQUEST_TIME);
            String requestType = csvRecord.get(REQUEST_TYPE);
            count++;
            TemporalAccessor date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Europe/Paris")).parse(requestTime);
            Instant instant = Instant.from(date);
            LOGGER.info("\tTrx {}. request type : {}, request time : {}, instant : {} ", count, requestType, requestTime, instant);
            //System.out.printf("\tTrx %d. request type : %s, request time : %s, instant : %s%n", count, requestType, requestTime, instant);
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