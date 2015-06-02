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
    File outputFile = new File(basedir+"/logs", "storage.txt");


    public void storeMetric(Metric m, String tenantId) throws FileNotFoundException {

        String name = m.getName();
        String category = m.getCategory();
        String metric = m.getMetric();
        String source = m.getSource();
        Long timestamp = m.getTimestamp();
        List<String> tags = m.getTags();
        List<Event> events = m.getEvents();
        List<Measurement> measurements = m.getMeasurements();

        StringBuffer sb = new StringBuffer();
/*        sb.append(new Date()).append(" ").append(Thread.currentThread().getId()).append(" ");
        sb.append("Metric received: " + name);*/

        logger.info("Tenant {} got a metric: {}", tenantId, name);

/*        try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(outputFile, true))) {
            printWriter.println(sb.toString());
        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        }*/
    }
}
