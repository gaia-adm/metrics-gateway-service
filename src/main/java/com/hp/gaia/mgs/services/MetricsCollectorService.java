package com.hp.gaia.mgs.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hp.gaia.mgs.amqp.AmqpManager;
import com.hp.gaia.mgs.dto.*;
import com.hp.gaia.mgs.dto.elasticsearch.ElasticSearchHandler;
import com.rabbitmq.client.AMQP;
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

    private final static String INFLUXDB_DB_NAME_PROPERTY = "dbname";
    AmqpManager amqpManager;

    private Boolean useAmqp = Boolean.valueOf(PropertiesKeeperService.getInstance().getEnvOrPropAsString("useAmqp"));
    private Boolean processEs = Boolean.valueOf(PropertiesKeeperService.getInstance().getEnvOrPropAsString("processEs"));
    private Boolean processInfluxDB = Boolean.valueOf(PropertiesKeeperService.getInstance().getEnvOrPropAsString("processInfluxDB"));

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

        System.out.println("processInfluxDB flag is " + processInfluxDB);
        if (processInfluxDB) {
            System.out.println("InfluxDB processing is activated");
        } else {
            System.out.println("InfluxDB processing is disabled");
        }
    }


    /**
     * Handles both ElasticSearch and InfluxDB events publishing
     *
     * @param events   - List of input events (json); events must extend {@link com.hp.gaia.mgs.dto.BaseEvent}
     * @param tenantId - tenant id reported the events
     * @throws JsonProcessingException
     * @see com.hp.gaia.mgs.dto.BaseEvent
     */
    public <T extends BaseEvent> void publishEvent(Collection<T> events, String tenantId) throws IOException, TimeoutException {

        logger.info("Going to publish {} events for tenant {}.", events.size(), tenantId);

        if (processInfluxDB) {
            handleInfluxDB(events, tenantId);
        }

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
        byte[] output = esHandler.convert(events);

        logger.debug("Publishing elasticSearch events for tenant {}: payload size is {} ", tenantId, output.length);
        logger.trace("Publishing elasticSearch events for tenant {}: {} ", tenantId, new String(output, "UTF-8"));
        if (useAmqp) {
            amqpManager.getEsChannel().basicPublish("", amqpManager.getEsQueueName(), null, output);
        }
        else {
            logger.info("dummy mode - did not send events to RabbitMQ, only print to log");
        }
        logger.info("Successfully published {} elasticSearch events for tenant {}.", events.size(), tenantId);
    }

    /*
     * Translate json formatted input to InfluxDB line protocol (https://influxdb.com/docs/v0.9/write_protocols/write_syntax.html) and publish to the system
     * and publish to RabbitMQ (or log)
     * InfluxDB line protocol requires the following format: [key] [fields] [timestamp]
     * For example: cpu,host=server\ 01,region=us-west cpu load=10.0,alert=true,reason="value above maximum threshold" 1434055562005
     * NOTE: timestamp is in milliseconds, so inserting data to InfluxDB requires precision=ms parameter (default is nanoseconds)
    * */
    private <T extends BaseEvent> void handleInfluxDB(Collection<T> events, String tenantId) throws IOException, TimeoutException {
        AMQP.BasicProperties.Builder propsBuilder = new AMQP.BasicProperties.Builder();
        Map<String, Object> map = new HashMap<>();
        map.put(INFLUXDB_DB_NAME_PROPERTY, tenantId);
        propsBuilder.headers(map);

        StringBuffer mainSb = new StringBuffer();

        InfluxLineProtocolConverterFactory converterFactory = new InfluxLineProtocolConverterFactory();
        for (T event : events) {
            if(event != null) {
                mainSb.append(converterFactory.getConverter(event.getType()).convert(event));
            }
        }

        logger.debug("Publishing influxdb events for tenant {}: payload size is {} ", tenantId, mainSb.toString().length());
        logger.trace("Publishing influxdb events for tenant {}: {} ", tenantId, mainSb);
        if (useAmqp) {
            amqpManager.getInfluxDbChannel().basicPublish("", amqpManager.getInfluxDbQueueName(),
                    propsBuilder.build(), mainSb.toString().getBytes());
        }
        else {
            logger.info("dummy mode - did not send events to RabbitMQ, only print to log");
        }
        logger.info("Successfully published {} influxdb events for tenant {}.", events.size(), tenantId);
    }

}

