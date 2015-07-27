package com.hp.gaia.mgs.dto;

import com.hp.gaia.mgs.dto.change.ChangeToInfluxLineProtocol;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by belozovs on 7/26/2015.
 */
public class InfluxLineProtocolConverterTest {

    @Test
    public void testGenerateDateNullInstanceId() throws Exception {

        String instanceId = "0";

        Long currentTime = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTime);

        Long longerTime = new ChangeToInfluxLineProtocol().generateUniqueTimestamp(currentTime);
        String longerTimeString = String.valueOf(longerTime);

        assertEquals("Six digits must be added", 6, longerTimeString.length() - currentTimeString.length());
        assertEquals("InstanceId should appear", ("000" + instanceId).substring(instanceId.length()), longerTimeString.substring(currentTimeString.length(), currentTimeString.length() + 3));
        System.out.println(longerTimeString);
    }

}


