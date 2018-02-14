package org.apache.camel.example.reload;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraProcessor implements Processor {

    private static final String JIRA_TASK_TEMP =
            "{\"fields\":{\"project\":{\"key\": \"NITRADEMO\"},\"summary\": \"Incident-Terminals with transaction gaps\",\"description\": \"${Description}\",\"issuetype\": {\"name\": \"Task\"}}}";
    private final static Logger LOGGER = LoggerFactory.getLogger(JiraProcessor.class);
    private static final String DESCRIPTION = "${Description}";

    @Override
    public void process(Exchange exchange) throws Exception {
        String inBody = exchange.getIn().getBody(String.class);
        LOGGER.info("Received in message : {}", inBody);
        String taskJson = JIRA_TASK_TEMP.replace(DESCRIPTION, "Terminals exceeding transaciton gap : " + inBody);
        LOGGER.info("JIRA Task JSON {}", taskJson);
        exchange.getIn().setBody(taskJson);
    }
}
