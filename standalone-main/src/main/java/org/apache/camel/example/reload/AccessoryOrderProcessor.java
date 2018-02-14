package org.apache.camel.example.reload;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessoryOrderProcessor implements Processor {

    private final static Logger LOGGER = LoggerFactory.getLogger(AccessoryOrderProcessor.class);
    private int paperPerTrx;
    private static final String DESCRIPTION = "${Description}";

    private static final String JIRA_TASK_TEMP =
            "{\"fields\":{\"project\":{\"key\": \"NITRADEMO\"},\"summary\": \"Incident-Terminals with transaction gaps\",\"description\": \"${Description}\",\"issuetype\": {\"name\": \"Task\"}}}";

    @Override
    public void process(Exchange exchange) throws Exception {
        String inBody = exchange.getIn().getBody(String.class);
        LOGGER.info("Received in message : {}", inBody);
        //int paperConsumed = numOfTrx * paperPerTrx;
        //LOGGER.info("Paper consumed {}", paperConsumed);
        String taskJson = JIRA_TASK_TEMP.replace(DESCRIPTION, "Terminals exceeding transaciton gap : " + inBody);
        LOGGER.info("JIRA Task JSON {}", taskJson);
        exchange.getIn().setBody(taskJson);
    }

    public int getPaperPerTrx() {
        return paperPerTrx;
    }

    public void setPaperPerTrx(int paperPerTrx) {
        this.paperPerTrx = paperPerTrx;
    }
}
