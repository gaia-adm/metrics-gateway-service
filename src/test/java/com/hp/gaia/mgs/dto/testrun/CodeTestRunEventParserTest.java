package com.hp.gaia.mgs.dto.testrun;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.BaseEvent;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by belozovs on 7/15/2015.
 */

public class CodeTestRunEventParserTest {

    private final String codeTestrun1 = "{\"event\":\"code_testrun\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"repository\":\"git://github.com/hp/mqm-server\",\"branch\":\"master\"},\"id\":{\"package\":\"com.hp.mqm\",\"class\":\"FilterBuilder\",\"method\":\"TestLogicalOperators\"},\"tags\":{\"build_job\":\"backend_job\",\"browser\":\"firefox\",\"build_label\":\"1.7.0\"},\"result\":{\"status\":\"error\",\"error\":\"NullPointerException: ...\",\"setup_time\":35,\"tear_down_time\":20,\"run_time\":130}}";

    private final String codeTestrun2 = "{\n" +
            "    \"time\": \"2015-08-17T03:16:02\",\n" +
            "    \"source\": {\n" +
            "      \"job_name\": \"test-executor\",\n" +
            "      \"root_job_name\": \"product-root\",\n" +
            "      \"build_server_uri\": \"http://my-jenkins:8080\",\n" +
            "      \"build_server_host\": \"my-jenkins\",\n" +
            "      \"source_type\": \"jenkins\"\n" +
            "    },\n" +
            "    \"event\": \"code_testrun\",\n" +
            "    \"tags\": {\n" +
            "      \"build_result\": \"UNSTABLE\",\n" +
            "      \"mytag2\": \"MYTAG2_VALUE\",\n" +
            "      \"mytag1\": \"MYTAG1_VALUE\",\n" +
            "      \"build_uri_path\": \"jobs/test-executor\"\n" +
            "    },\n" +
            "    \"result\": {\n" +
            "      \"run_time\": 884.192,\n" +
            "      \"failed_since\": 0,\n" +
            "      \"skipped\": false,\n" +
            "      \"skipped_message\": null,\n" +
            "      \"age\": 0,\n" +
            "      \"status\": \"PASSED\",\n" +
            "      \"error\": null\n" +
            "    },\n" +
            "    \"id\": {\n" +
            "      \"root_build_number\": \"40\",\n" +
            "      \"method\": \"run[0]\",\n" +
            "      \"build_number\": \"50\",\n" +
            "      \"class\": \"SanityStories\",\n" +
            "      \"package\": \"com.sample.configuration\"\n" +
            "    }\n" +
            "  }";

    @Test
    public void testCodeTestrun1() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        List<BaseEvent> events = mapper.readValue("[" + codeTestrun1 + "]", new TypeReference<List<BaseEvent>>() {
        });

        assertEquals("Single event in the list", 1, events.size());
        assertEquals("Event of type code_testrun", CodeTestRunEvent.EVENT_TYPE, events.get(0).getType());
        assertEquals("Three tags presented", 3, (events.get(0)).getTags().size());
        assertEquals("Two items in source presented", 2, (events.get(0)).getSource().size());
        assertEquals("Five items in result presented", 5, ((CodeTestRunEvent) (events.get(0))).getResult().getMembersAsMap().size());
    }

    @Test
    public void testCodeTestrun2() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        List<BaseEvent> events = mapper.readValue("[" + codeTestrun2 + "]", new TypeReference<List<BaseEvent>>() {
        });

        assertEquals("Single event in the list", 1, events.size());
        assertEquals("Event of type code_testrun", CodeTestRunEvent.EVENT_TYPE, events.get(0).getType());
        assertEquals("Four tags presented", 4, (events.get(0)).getTags().size());
        assertEquals("Five items in source presented", 5, (events.get(0)).getSource().size());
        assertEquals("Five items in result presented", 6, ((CodeTestRunEvent) (events.get(0))).getResult().getMembersAsMap().size());
    }
}
