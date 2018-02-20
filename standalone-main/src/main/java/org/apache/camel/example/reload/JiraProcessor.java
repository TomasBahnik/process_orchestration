package org.apache.camel.example.reload;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraProcessor implements Processor {

    private static final String DESCRIPTION = "${Description}";
    private static final String INCIDENT_TERMINALS_WITH_TRANSACTION_GAPS = "Incident-Terminals with transaction gaps";
    private static final String JIRA_PROJECT = "NITRADEMO";
    private static final String JIRA_TASK_TEMP_GAP =
            "{\"fields\":{\"project\":{\"key\": \"" + JIRA_PROJECT + "\"},\"summary\": \"" + INCIDENT_TERMINALS_WITH_TRANSACTION_GAPS +
                    "\",\"description\": " + "\"" + DESCRIPTION + "\",\"issuetype\": {\"name\": \"Task\"}}}";
    private static final String INCIDENT_TERMINALS_WITH_DATA_USAGE = "Incident-Terminals data usage exceeded";
    private static final String JIRA_TASK_TEMP_DATA_USAGE =
            "{\"fields\":{\"project\":{\"key\": \"" + JIRA_PROJECT + "\"},\"summary\": \"" + INCIDENT_TERMINALS_WITH_DATA_USAGE +
                    "\",\"description\": " + "\"" + DESCRIPTION + "\",\"issuetype\": {\"name\": \"Task\"}}}";
    private final static Logger LOGGER = LoggerFactory.getLogger(JiraProcessor.class);
    private static final String TERMINALS_EXCEEDING_TRANSACTION_GAP = "Terminals exceeding transaction gap";
    private static final String TERMINALS_DATA_USAGE = "Terminals data usage";

    @Override
    public void process(Exchange exchange) throws Exception {
        String inBody = exchange.getIn().getBody(String.class);
        String operationName = exchange.getProperty(MaxGapProcessor.OPERATION_NAME, String.class);
        if (MaxGapProcessor.MAX_GAP_OPERATION.equals(operationName)) {
            int maxGapInSeconds = exchange.getProperty(MaxGapProcessor.MAX_GAP_PROCESSOR_MAX_GAP_IN_SECONDS, Integer.class);
            String transactionDate = exchange.getProperty(MaxGapProcessor.MAX_GAP_PROCESSOR_TRANSACTION_DATE, String.class);
            LOGGER.info("Max Gap = {} sec, Transaction Date = {}", maxGapInSeconds, transactionDate);
            LOGGER.info(TERMINALS_EXCEEDING_TRANSACTION_GAP + " : {}", inBody);
            String taskJson = JIRA_TASK_TEMP_GAP.replace(DESCRIPTION, TERMINALS_EXCEEDING_TRANSACTION_GAP +
                    "(Max Gap : " + maxGapInSeconds + "sec, Transaction Date : " + transactionDate + ") "
                    + inBody);
            LOGGER.info("Payload sent to JIRA {}", taskJson);
            exchange.getIn().setBody(taskJson);
        }
        if (DataUsageProcessor.DATA_USAGE_OPERATION.equals(operationName)) {
            LOGGER.info(TERMINALS_DATA_USAGE + " : {}", inBody);
            String taskJson = JIRA_TASK_TEMP_DATA_USAGE.replace(DESCRIPTION, TERMINALS_DATA_USAGE + " : " + inBody);
            LOGGER.info("Payload sent to JIRA {}", taskJson);
            exchange.getIn().setBody(taskJson);
        }
    }
}
