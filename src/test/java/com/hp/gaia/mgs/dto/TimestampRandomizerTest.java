package com.hp.gaia.mgs.dto;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by belozovs on 7/26/2015.
 */
public class TimestampRandomizerTest {

    TimestampRandomizer tr = TimestampRandomizer.getInstance();
    int threadCount = 5000;

    @Test
    public void testPartOfIp() throws Exception {

        assertTrue("last part of ip returned - should be between 0 and 256", tr.getPartOfIp() >= 0 && tr.getPartOfIp() < 256);
    }

    @Test
    public void testNextNumberMultiThread() throws Exception {

        Runnable task = new Runnable() {
            @Override
            public void run() {
                int next = tr.nextNumber();
                assertNotNull("Next must not be null", next);
                assertTrue("Next must equal or bigger than than 0", next >= 0);
                assertTrue("Next must be lower than 1000", next < 1000);
            }

            ;
        };

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount / 3);
        List<Callable<Object>> tasks = Collections.nCopies(threadCount, Executors.callable(task));
        executorService.invokeAll(tasks);

    }
}