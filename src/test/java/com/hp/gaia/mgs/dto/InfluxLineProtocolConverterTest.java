package com.hp.gaia.mgs.dto;

import com.hp.gaia.mgs.dto.change.ChangeToInfluxLineProtocol;
import com.hp.gaia.mgs.dto.generalevent.GeneralEvent;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void testConversion() throws Exception {

        GeneralEvent ge = new GeneralEvent();
        Map<String, Object> idMap = new HashMap<>();
        idMap.put("uuid",1111);
        ge.setId(idMap);
        Map<String, String> sourceMap = new HashMap<>();
        sourceMap.put("country", "Spain");
        sourceMap.put("city", "Barcelona");
        ge.setSource(sourceMap);
        Map<String, String> tagsMap = new HashMap<>();
        tagsMap.put("language", "Catalan");
        ge.setTags(tagsMap);
        ge.setType(GeneralEvent.EVENT_TYPE);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("temp", 32);
        dataMap.put("rainy", false);
        ge.setData(dataMap);
        ge.setTime(new Date(1442397531403L));

        InfluxLineProtocolConverterFactory converterFactory = new InfluxLineProtocolConverterFactory();
        String eventStr = converterFactory.getConverter(ge.getType()).convert(ge);

        //cut \r\n in the end of the actual output
        BufferedReader bufferedReader = new BufferedReader(new StringReader(eventStr));
        String eventWithNoNewLineChars = bufferedReader.readLine();
        bufferedReader.close();

        //cut TimestampRandomizer impact (nanoseconds is a running number, microseconds are IP-based)
        String actualEvent = eventWithNoNewLineChars.substring(0, eventWithNoNewLineChars.length()-6);

        String expectedOutput = "general,_country=Spain,_city=Barcelona,_language=Catalan,_dimension=general _uuid=1111,_rainy=false,_temp=32 1442397531403";
        assertEquals("Expected output check", expectedOutput, actualEvent);
    }

}


