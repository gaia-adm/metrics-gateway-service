package com.hp.gaia.mgs.dto.elasticsearch;

import com.hp.gaia.mgs.dto.BaseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by tsadok on 27/11/2015.
 */
public class ElasticSearchHandlerTest {

    @Test
    public void testConvert() throws Exception {
        String jsonEvent = "[{\"event\":\"code_testrun\",\"id\":{\"method\":\"test[418: combination of <[Passed, Pending, Passed, Passed]>]\",\"build_number\":\"29\",\"class\":\"ActivityStatusCommutativeTest\",\"package\":\"com.hp.alm.platform.dataflow\",\"root_build_number\":\"29\"},\"tags\":{\"scm_branch\":null,\"build_result\":\"UNSTABLE\",\"flow_type\":null,\"build_uri_path\":\"job/ALM-Newton-Compile-Server/29/\",\"schema_version\":null},\"result\":{\"status\":\"PASSED\",\"skipped\":false,\"run_time\":0,\"failed_since\":0,\"age\":0,\"error\":null,\"skipped_message\":null},\"source\":{\"root_job_name\":\"ALM-Newton-Compile-Server\",\"build_server_uri\":\"http://mydtbld0048.isr.hp.com:8888/jenkins\",\"job_name\":\"ALM-Newton-Compile-Server\",\"source_type\":\"jenkins\",\"build_server_host\":\"mydtbld0048.isr.hp.com\"},\"time\":\"2015-08-20T10:37:41\"}]";

        List<BaseEvent> receivedEvent = null;
        receivedEvent = new ObjectMapper().readValue(jsonEvent, new TypeReference<List<BaseEvent>>(){});

        ElasticSearchHandler esh = new ElasticSearchHandler();
        byte[] output = esh.convert(receivedEvent, "tenant_123");

        String outputString = new String(output, "UTF-8");
        assertEquals("number of '\\n' should be 2", 2, StringUtils.countOccurrencesOf(outputString, "\n"));
        assertEquals("time field should be presented", 1, StringUtils.countOccurrencesOf(outputString, "\"timestamp\":"));
    }
}
