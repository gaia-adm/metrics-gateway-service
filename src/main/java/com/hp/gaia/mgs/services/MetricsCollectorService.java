package com.hp.gaia.mgs.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hp.gaia.mgs.amqp.AmqpManager;
import com.hp.gaia.mgs.dto.*;
import com.rabbitmq.client.AMQP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by belozovs on 5/27/2015.
 */
public class MetricsCollectorService {

    private final static Logger logger = LoggerFactory.getLogger(MetricsCollectorService.class);

    private final static String DB_NAME_PROPERTY = "dbname";
    AmqpManager amqpManager;

    public MetricsCollectorService() throws IOException {
        this.amqpManager = new AmqpManager();
    }

    String basedir = System.getProperty("user.dir");


    public void storeMetric(OldMetric m) {

        String name = m.getName();
        String category = m.getCategory();
        String metric = m.getMetric();
        String source = m.getSource();
        Long timestamp = m.getTimestamp();
        List<String> tags = m.getTags();
        List<OldEvent> events = m.getEvents();
        List<OldMeasurement> measurements = m.getMeasurements();

        logger.info("OldMetric received: {}", name);
    }

    public void storeMetric(String thingToPrint) {
        logger.info("Received metrics - " + thingToPrint);
    }

    public void storeMetric(String numOfMetrics, Integer tenantId) {
        logger.info("Tenant " + tenantId + " received " + numOfMetrics + " metrics");
    }

    public void publishMetric(String metric, String tenantIdString) throws IOException, TimeoutException {
        AMQP.BasicProperties.Builder propsBuilder = new AMQP.BasicProperties.Builder();
        Map map = new HashMap<String, Object>();
        map.put(DB_NAME_PROPERTY, tenantIdString);
        propsBuilder.headers(map);
        amqpManager.getChannel().basicPublish("", amqpManager.getQueueName(), propsBuilder.build(), metric.getBytes());
        System.out.println(" [x] Sent '" + metric + "'");

    }

    /**
     * Test method that can be used for running the service locally.
     * Method just prints some data to STDOUT instead of publishing it to RabbitMQ in the full blown system
     *
     * @param events   - List of input events (json); events must extend {@link com.hp.gaia.mgs.dto.BaseEvent}
     * @param tenantId - tenant id reported the events
     */
    public <T extends BaseEvent> void storeEvent(Collection<T> events, String tenantId) {

        logger.info("Going to publish {} events for tenant {}.", events.size(), tenantId);

        StringBuffer mainSb = new StringBuffer();

        InfluxLineProtocolConverterFactory converterFactory = new InfluxLineProtocolConverterFactory();
        for (T event : events) {
            mainSb.append(converterFactory.getConverter(event.getType()).convert(event));
        }

        logger.debug("Publishing event for tenant {}: payload size is {} ", tenantId, mainSb.toString().length());
        logger.trace("Publishing event for tenant {}: {} ", tenantId, mainSb);

        logger.info("Successfully published {} events for tenant {}.", events.size(), tenantId);
    }

    /**
     * Translate json formatted input to InfluxDB line protocol (https://influxdb.com/docs/v0.9/write_protocols/write_syntax.html) and publish to the system
     * InfluxDB line protocol requires the following format: [key] [fields] [timestamp]
     * For example: cpu,host=server\ 01,region=us-west cpu load=10.0,alert=true,reason="value above maximum threshold" 1434055562005
     * NOTE: timestamp is in milliseconds, so inserting data to InfluxDB requires precision=ms parameter (default is nanoseconds)
     * <p>
     * Multiple events are published one by one to RabbitMQ.
     *
     * @param events   - List of input events (json); events must extend {@link com.hp.gaia.mgs.dto.BaseEvent}
     * @param tenantId - tenant id reported the events
     * @throws JsonProcessingException
     * @see com.hp.gaia.mgs.dto.BaseEvent
     */
    public <T extends BaseEvent> void publishEvent(Collection<T> events, String tenantId) throws IOException, TimeoutException {

        logger.info("Going to publish {} events for tenant {}.", events.size(), tenantId);

        AMQP.BasicProperties.Builder propsBuilder = new AMQP.BasicProperties.Builder();
        Map<String, Object> map = new HashMap<>();
        map.put(DB_NAME_PROPERTY, tenantId);
        propsBuilder.headers(map);

        StringBuffer mainSb = new StringBuffer();

        InfluxLineProtocolConverterFactory converterFactory = new InfluxLineProtocolConverterFactory();
        for (T event : events) {
            mainSb.append(converterFactory.getConverter(event.getType()).convert(event));
        }

        logger.debug("Publishing event for tenant {}: payload size is {} ", tenantId, mainSb.toString().length());
        logger.trace("Publishing event for tenant {}: {} ", tenantId, mainSb);
        amqpManager.getChannel().basicPublish("", amqpManager.getQueueName(), propsBuilder.build(), mainSb.toString().getBytes());
        logger.debug(" [v] Sent event for tenant {}. ", tenantId);

        logger.info("Successfully published {} events for tenant {}.", events.size(), tenantId);
    }

}

