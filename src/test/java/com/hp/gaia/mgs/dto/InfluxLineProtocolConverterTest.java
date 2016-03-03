package com.hp.gaia.mgs.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.change.DeserializationUtils;
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
    public void testEscaping() throws Exception {
        assertEquals("Check all required escaping", "this\\ is\\ it\\,\\ \\=\\ \\\"my\\\"\\ \\'test\\'", new DeserializationUtils().getEscapedString("this is it, = \"my\" 'test'"));
    }

    @Test
    public void testConversionCodeTestRunEvent() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(codeTestrun, CodeTestRunEvent.class);
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
