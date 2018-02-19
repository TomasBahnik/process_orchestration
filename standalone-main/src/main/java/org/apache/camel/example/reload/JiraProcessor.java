package org.apache.camel.example.reload;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraProcessor implements Processor {

    private static final String DESCRIPTION = "${Description}";
    private static final String INCIDENT_TERMINALS_WITH_TRANSACTION_GAPS = "Incident-Terminals with transaction gaps";
    private static final String JIRA_PROJECT = "NITRADEMO";
    private static final String JIRA_TASK_TEMP =
            "{\"fields\":{\"project\":{\"key\": \"" + JIRA_PROJECT + "\"},\"summary\": \"" + INCIDENT_TERMINALS_WITH_TRANSACTION_GAPS +
                    "\",\"description\": " + "\"" + DESCRIPTION + "\",\"issuetype\": {\"name\": \"Task\"}}}";
    private final static Logger LOGGER = LoggerFactory.getLogger(JiraProcessor.class);
    private static final String TERMINALS_EXCEEDING_TRANSACTION_GAP = "Terminals exceeding transaction gap";

    @Override
    public void process(Exchange exchange) throws Exception {
        String inBody = exchange.getIn().getBody(String.class);
        int maxGapInSeconds = exchange.getProperty(CsvProcessor.CSV_PROCESSOR_MAX_GAP_IN_SECONDS, Integer.class);
        String transactionDate = exchange.getProperty(CsvProcessor.CSV_PROCESSOR_TRANSACTION_DATE, String.class);
        LOGGER.info("maxGapInSeconds = {}, transactionDate = {}", maxGapInSeconds, transactionDate);
        LOGGER.info(TERMINALS_EXCEEDING_TRANSACTION_GAP + " : {}", inBody);
        String taskJson = JIRA_TASK_TEMP.replace(DESCRIPTION, TERMINALS_EXCEEDING_TRANSACTION_GAP +
                "(max gap : " + maxGapInSeconds + "[sec], Transaction Date : " + transactionDate + ") "
                + inBody);
        LOGGER.info("Payload sent to JIRA {}", taskJson);
        exchange.getIn().setBody(taskJson);
    }
}
