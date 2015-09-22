package com.hp.gaia.mgs.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.change.ChangeToInfluxLineProtocol;
import com.hp.gaia.mgs.dto.generalevent.GeneralEvent;
import com.hp.gaia.mgs.dto.testrun.CodeTestRunEvent;
import org.junit.Test;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    public void testConversionGeneralEvent() throws Exception {

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

    @Test
    public void testConversionCodeTestRunEvent() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        CodeTestRunEvent event = mapper.readValue(codeTestrun, CodeTestRunEvent.class);

        InfluxLineProtocolConverterFactory converterFactory = new InfluxLineProtocolConverterFactory();
        String eventStr = converterFactory.getConverter(event.getType()).convert(event);

        //cut \r\n in the end of the actual output
        BufferedReader bufferedReader = new BufferedReader(new StringReader(eventStr));
        String eventWithNoNewLineChars = bufferedReader.readLine();
        bufferedReader.close();

        //cut TimestampRandomizer impact (nanoseconds is a running number, microseconds are IP-based)
        String actualEvent = eventWithNoNewLineChars.substring(0, eventWithNoNewLineChars.length()-6);

        String expectedOutput = "code_testrun,_job_name=test-executor,_source_type=jenkins,_source_null=\"\",_build_result=UNSTABLE,_null_tag=\"\",_dimension=result _package=\"com.sample.configuration\",_method=\"run[0]\",_class=\"SanityStories\",_runTime=884,_failed_since=0,_status=\"PASSED\",_skipped=false,_skipped_message=\"\" 1439781362000";
        assertEquals("Expected output check", expectedOutput, actualEvent);
    }

    private final String codeTestrun = "{\n" +
            "    \"time\": \"2015-08-17T03:16:02Z\",\n" +
            "    \"source\": {\n" +
            "      \"job_name\": \"test-executor\",\n" +
            "      \"source_type\": \"jenkins\"\n," +
            "      \"source_null\": null\n" +
            "    },\n" +
            "    \"event\": \"code_testrun\",\n" +
            "    \"tags\": {\n" +
            "      \"build_result\": \"UNSTABLE\"\n," +
            "      \"null_tag\": null\n" +
            "    },\n" +
            "    \"result\": {\n" +
            "      \"run_time\": 884.192,\n" +
            "      \"failed_since\": 0,\n" +
            "      \"skipped\": false,\n" +
            "      \"skipped_message\": null,\n" +
            "      \"status\": \"PASSED\",\n" +
            "      \"error\": null\n" +
            "    },\n" +
            "    \"id\": {\n" +
            "      \"method\": \"run[0]\",\n" +
            "      \"class\": \"SanityStories\",\n" +
            "      \"package\": \"com.sample.configuration\"\n" +
            "    }\n" +
            "  }";
}
