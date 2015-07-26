package com.hp.gaia.mgs.dto;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by belozovs on 7/26/2015.
 *
 */

public class TimestampRandomizer {

    private static final int MAX_NUMBER = 1000;
    private static final AtomicInteger runningNumber = new AtomicInteger(0);

    private static TimestampRandomizer instance;

    public static TimestampRandomizer getInstance(){
        if(instance == null){
            instance = new TimestampRandomizer();
        }
        return instance;
    }

    private TimestampRandomizer() {}

    public int nextNumber() {
        for (;;) {
            int current = runningNumber.get();
            int next = (current + 1) % MAX_NUMBER;
            if (runningNumber.compareAndSet(current, next))
                return current;
        }
    }


}
