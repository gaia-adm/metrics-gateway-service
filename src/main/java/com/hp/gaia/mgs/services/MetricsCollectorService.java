package com.hp.gaia.mgs.services;

import com.hp.gaia.mgs.amqp.AmqpManager;
import com.hp.gaia.mgs.dto.Event;
import com.hp.gaia.mgs.dto.Measurement;
import com.hp.gaia.mgs.dto.Metric;
import com.hp.gaia.mgs.spring.MyCustomException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by belozovs on 5/27/2015.
 */
public class MetricsCollectorService {

    private final static String DB_NAME_PROPERTY = "dbname";
    private final static Logger logger = LoggerFactory.getLogger(MetricsCollectorService.class);
    private AmqpManager amqpManager;

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
        Map map = new HashMap<String,Object>();
        map.put(DB_NAME_PROPERTY, tenantIdString);
        propsBuilder.headers(map);
        amqpManager.getChannel().basicPublish("", amqpManager.getQueueName(), propsBuilder.build(), metric.getBytes());
        System.out.println(" [x] Sent '" + metric + "'");

    }
}
