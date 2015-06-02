package com.hp.gaia.mgs.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TimerTask;

/**
 * Created by belozovs on 5/31/2015.
 */
public class MonitoringService extends TimerTask{

    private final static Logger logger = LoggerFactory.getLogger(MonitoringService.class);

    private static MonitoringService instance;
    private Runtime runtime;

    private MonitoringService() {
        runtime = Runtime.getRuntime();
    }

    public static MonitoringService getIntstance() {
        if (instance == null) {
            instance = new MonitoringService();
        }
        return instance;
    }

    @Override
    public void run() {
        logger.info("Free memory: {} Used memory: {} Thread counter: {}", runtime.freeMemory(), (runtime.totalMemory()-runtime.freeMemory()), Thread.activeCount());
//        System.out.println(new Date() + " Free memory: " + runtime.freeMemory() + " Used memory: " + (runtime.totalMemory()-runtime.freeMemory()) + " Thread counter: " + Thread.activeCount());
    }

    @Override
    public boolean cancel() {
        instance = null;
        return super.cancel();
    }

}
