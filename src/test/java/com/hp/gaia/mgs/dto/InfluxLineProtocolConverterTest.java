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

        Long currentTime = System.currentTimeMillis();
        String currentTimeString = String.valueOf(currentTime);

        Long longerTime = new ChangeToInfluxLineProtocol().generateUniqueTimestamp(currentTime);
        String longerTimeString = String.valueOf(longerTime);

        assertEquals("Six digits must be added", 6, longerTimeString.length() - currentTimeString.length());
        System.out.println(longerTimeString);
    }

    @Test
    public void testEscaping() throws Exception {
        assertEquals("Check all required escaping", "this\\ is\\ it\\,\\ \\=\\ \\\"my\\\"\\ \\'test\\'", new ChangeToInfluxLineProtocol().getEscapedString("this is it, = \"my\" 'test'"));
    }

    @Test
    public void testEscapingWithDefaultPrefix() throws Exception {
        assertEquals("Check all required escaping", InfluxLineProtocolConverter.DB_FIELD_PREFIX + "this\\ is\\ it\\,\\ \\=\\ \\\"my\\\"\\ \\'test\\'", new ChangeToInfluxLineProtocol().getEscapedStringWithPrefix("this is it, = \"my\" 'test'"));
    }

    @Test
    public void testEscapingWithNonDefaultPrefix() throws Exception {
        String prefix = "aaa";
        assertEquals("Check all required escaping", prefix + "this\\ is\\ it\\,\\ \\=\\ \\\"my\\\"\\ \\'test\\'", new ChangeToInfluxLineProtocol().getEscapedStringWithPrefix("this is it, = \"my\" 'test'", prefix));
    }

}


