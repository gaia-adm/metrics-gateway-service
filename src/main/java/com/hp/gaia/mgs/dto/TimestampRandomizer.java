package com.hp.gaia.mgs.dto;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by belozovs on 7/26/2015.
 *
 * Helper class to the timestamp unique
 * partOfIp used to populate microseconds
 * running number between 0 and 1000 used to populate nanoseconds
 *
 */

public class TimestampRandomizer {

    private static final int MAX_NUMBER = 1000;
    private static final AtomicInteger runningNumber = new AtomicInteger(0);

    private static TimestampRandomizer instance;

    private int partOfIp = 0;

    public static TimestampRandomizer getInstance(){
        if(instance == null){
            instance = new TimestampRandomizer();
        }
        return instance;
    }

    private TimestampRandomizer() {
        try {
            String addr = InetAddress.getLocalHost().getHostAddress();
            int startPos = addr.lastIndexOf(".");
            partOfIp = Integer.valueOf(addr.substring(startPos+1));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public int nextNumber() {
        for (;;) {
            int current = runningNumber.get();
            int next = (current + 1) % MAX_NUMBER;
            if (runningNumber.compareAndSet(current, next))
                return current;
        }
    }


    public int getPartOfIp() {
        return partOfIp;
    }

}
