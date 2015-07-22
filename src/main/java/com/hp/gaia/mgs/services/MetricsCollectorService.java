package com.hp.gaia.mgs.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hp.gaia.mgs.amqp.AmqpManager;
import com.hp.gaia.mgs.dto.AbstractBaseEvent;
import com.hp.gaia.mgs.dto.Event;
import com.hp.gaia.mgs.dto.Measurement;
import com.hp.gaia.mgs.dto.Metric;
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


    public void storeMetric(Metric m) {

        String name = m.getName();
        String category = m.getCategory();
        String metric = m.getMetric();
        String source = m.getSource();
        Long timestamp = m.getTimestamp();
        List<String> tags = m.getTags();
        List<Event> events = m.getEvents();
        List<Measurement> measurements = m.getMeasurements();

        logger.info("Metric received: {}", name);
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
     * @param events   - List of input events (json); events must extend {@link com.hp.gaia.mgs.dto.AbstractBaseEvent}
     * @param tenantId - tenant id reported the events
     */
    public <T extends AbstractBaseEvent> void storeEvent(Collection<T> events, String tenantId) {

        logger.info("Going to publish {} events for tenant {}.", events.size(), tenantId);

        for (T event : events) {

            StringBuffer mainSb = new StringBuffer();
            //tags and source
            mainSb.append(escapedString(event.getType()));
            for (String key : event.getSource().keySet()) {
                mainSb.append(",").append(escapedString(key)).append("=").append(escapedString(event.getSource().get(key)));
            }
            for (String key : event.getTags().keySet()) {
                mainSb.append(",").append(escapedString(key)).append("=").append(escapedString(event.getTags().get(key)));
            }

            StringBuffer valuesSb = new StringBuffer();
            //id and values (fields, result, etc.)
            for (String key : event.getId().keySet()) {
                //note: id value is always String and should be quoted (at least thanks to id_ prefix)
                valuesSb.append(escapedString(key)).append("=").append(quoteValue("id_" + event.getId().get(key))).append(",");
            }
            for (String key : event.getValues().keySet()) {
                if (event.getValues().get(key).getClass().equals(java.lang.String.class)) {
                    //double quote value if its type is String
                    valuesSb.append(escapedString(key)).append("=").append(quoteValue((String) event.getValues().get(key))).append(",");
                } else {
                    valuesSb.append(escapedString(key)).append("=").append(event.getValues().get(key)).append(",");
                }
            }
            if (valuesSb.length() > 0 && valuesSb.charAt(valuesSb.length() - 1) == ',') {
                valuesSb.setLength(valuesSb.length() - 1);
            }
            //add timestamp
            //TBD - boris: make InfluxDBManager in event-indexer adding &precision=ms query param to "writeToDB" URL and remove 1000000 from here
            valuesSb.append(" ").append(event.getTime().getTime());

            String result = mainSb.append(" ").append(valuesSb).toString();

            logger.debug("Publishing event for tenant {}: {}: ", tenantId, result);



        }
        logger.info("Successfully published {} events for tenant {}.", events.size(), tenantId);
    }

    /**
     * Translate json formatted input to InfluxDB line protocol (https://influxdb.com/docs/v0.9/write_protocols/write_syntax.html) and publish to the system
     * InfluxDB line protocol requires the following format: [key] [fields] [timestamp]
     * For example: cpu,host=server\ 01,region=us-west cpu load=10.0,alert=true,reason="value above maximum threshold" 1434055562005
     * NOTE: timestamp is in milliseconds, so inserting data to InfluxDB requires precision=ms parameter (default is nanoseconds)
     *
     * Multiple events are published one by one to RabbitMQ.
     *
     * @param events   - List of input events (json); events must extend {@link com.hp.gaia.mgs.dto.AbstractBaseEvent}
     * @param tenantId - tenant id reported the events
     * @throws JsonProcessingException
     * @see com.hp.gaia.mgs.dto.AbstractBaseEvent
     */
    public <T extends AbstractBaseEvent> void publishEvent(Collection<T> events, String tenantId) throws IOException, TimeoutException {

        logger.info("Going to publish {} events for tenant {}.", events.size(), tenantId);

        AMQP.BasicProperties.Builder propsBuilder = new AMQP.BasicProperties.Builder();
        Map map = new HashMap<String, Object>();
        map.put(DB_NAME_PROPERTY, tenantId);
        propsBuilder.headers(map);

        for (T event : events) {

            StringBuffer mainSb = new StringBuffer();
            //tags and source
            mainSb.append(escapedString(event.getType()));
            for (String key : event.getSource().keySet()) {
                mainSb.append(",").append(escapedString(key)).append("=").append(escapedString(event.getSource().get(key)));
            }
            for (String key : event.getTags().keySet()) {
                mainSb.append(",").append(escapedString(key)).append("=").append(escapedString(event.getTags().get(key)));
            }

            StringBuffer valuesSb = new StringBuffer();
            //id and values (fields, result, etc.)
            for (String key : event.getId().keySet()) {
                //note: id value is always String and should be quoted (at least thanks to id_ prefix)
                valuesSb.append(escapedString(key)).append("=").append(quoteValue("id_" + event.getId().get(key))).append(",");
            }
            for (String key : event.getValues().keySet()) {
                if (event.getValues().get(key).getClass().equals(java.lang.String.class)) {
                    //double quote value if its type is String
                    valuesSb.append(escapedString(key)).append("=").append(quoteValue((String) event.getValues().get(key))).append(",");
                } else {
                    valuesSb.append(escapedString(key)).append("=").append(event.getValues().get(key)).append(",");
                }
            }
            if (valuesSb.length() > 0 && valuesSb.charAt(valuesSb.length() - 1) == ',') {
                valuesSb.setLength(valuesSb.length() - 1);
            }
            //add timestamp
            //TBD - boris: make InfluxDBManager in event-indexer adding &precision=ms query param to "writeToDB" URL and remove 1000000 from here
            valuesSb.append(" ").append(event.getTime().getTime() * 1000000); //switch to nanoseconds, as InfluxDB requires

            String result = mainSb.append(" ").append(valuesSb).toString();

            logger.debug("Publishing event for tenant {}: {}. ", tenantId, result);
            amqpManager.getChannel().basicPublish("", amqpManager.getQueueName(), propsBuilder.build(), result.getBytes());
            logger.debug(" [v] Sent event for tenant {}. ", tenantId);



        }
        logger.info("Successfully published {} events for tenant {}.", events.size(), tenantId);
    }

    /**
     * Following characters must be escaped for InfluxDB: spaces, commas and double-quotes
     *
     * @param str String to replace spaces, commas and double-quotes
     * @return String after the replacement
     */
    String escapedString(String str) {
        return str.replace("\"", "\\\"").replace(" ", "\\ ").replace(",", "\\,");
    }

    /**
     * String values must be double-quoted when inserting to InfluxDB
     *
     * @param str String value to be double-quoated
     * @return double-quoted value
     */
    String quoteValue(String str) {
        return "\"" + str + "\"";
    }

}

