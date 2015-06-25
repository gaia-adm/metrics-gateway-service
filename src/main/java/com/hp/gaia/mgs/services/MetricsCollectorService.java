package com.hp.gaia.mgs.services;

import com.hp.gaia.mgs.dto.Event;
import com.hp.gaia.mgs.dto.Measurement;
import com.hp.gaia.mgs.dto.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

/**
 * Created by belozovs on 5/27/2015.
 */
public class MetricsCollectorService {

    private final static Logger logger = LoggerFactory.getLogger(MetricsCollectorService.class);


    String basedir = System.getProperty("user.dir");


    public void storeMetric(Metric m)  {

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
}
