package com.hp.gaia.mgs.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hp.gaia.mgs.amqp.AmqpManager;
import com.hp.gaia.mgs.dto.*;
import com.hp.gaia.mgs.dto.elasticsearch.ElasticSearchHandler;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by belozovs on 5/27/2015.
 */
public class MetricsCollectorService {

    private final static Logger logger = LoggerFactory.getLogger(MetricsCollectorService.class);

    AmqpManager amqpManager;

    private Boolean useAmqp = Boolean.valueOf(PropertiesKeeperService.getInstance().getEnvOrPropAsString("useAmqp"));
    private Boolean processEs = Boolean.valueOf(PropertiesKeeperService.getInstance().getEnvOrPropAsString("processEs"));

    public MetricsCollectorService() throws IOException {
        this.amqpManager = new AmqpManager();

        System.out.println("useAmqp flag is " + useAmqp);
        if (useAmqp) {
            System.out.println("Metrics will be published to RabbitMQ");
        } else {
            System.out.println("Metrics will be published to log file");
        }

        System.out.println("processEs flag is " + processEs);
        if (processEs) {
            System.out.println("ElasticSearch processing is activated");
        } else {
            System.out.println("ElasticSearch processing is disabled");
        }
    }


    /**
     * Handles  ElasticSearch events publishing
     *
     * @param events   - List of input events (json); events must extend {@link com.hp.gaia.mgs.dto.BaseEvent}
     * @param tenantId - tenant id reported the events
     * @throws JsonProcessingException
     * @see com.hp.gaia.mgs.dto.BaseEvent
     */
    public <T extends BaseEvent> void publishEvent(Collection<T> events, String tenantId) throws IOException, TimeoutException {

        logger.info("Going to publish {} events for tenant {}.", events.size(), tenantId);

        if (processEs)
        {
            handleEs(events, tenantId);
        }
    }

    /*
     * Translate json formatted input to ElasticSearch bulk protocol (https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html)
     * and publish to RabbitMQ (or log)
    * */
    private <T extends BaseEvent> void handleEs(Collection<T> events, String tenantId) throws IOException, TimeoutException {
        ElasticSearchHandler esHandler = new ElasticSearchHandler();

        for (T event: events) {
            byte[] output = esHandler.convert(event);

            logger.debug("Publishing elasticSearch event for tenant {}: payload size is {} ", tenantId, output.length);
            logger.trace("Publishing elasticSearch event for tenant {}: {} ", tenantId, new String(output, "UTF-8"));
            if (useAmqp) {

                //route key in the format of "event.TENANTID.DATASOURCE.EVENTTYPE"
                String routingKey = new StringBuilder().append("event.").append(tenantId).append(".all.").
                                                                        append(event.getType()).toString();

                //MINIMAL_BASIC makes sure the deliveryMode is 2, means - the message will be persistent
                amqpManager.getEsChannel().basicPublish(amqpManager.getExchangeName(), routingKey,
                        MessageProperties.MINIMAL_BASIC, output);
            } else {
                logger.info("dummy mode - did not send event to RabbitMQ, only print to log");
            }
        }
        logger.info("Successfully published {} elasticSearch events for tenant {}.", events.size(), tenantId);
    }
}

