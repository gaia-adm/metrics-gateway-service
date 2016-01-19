package com.hp.gaia.mgs.dto.elasticsearch;

import com.hp.gaia.mgs.dto.BaseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.util.StringUtils;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by tsadok on 27/11/2015.
 */
public class ElasticSearchHandlerTest {

    @Test
    public void testConvert() throws Exception {
        String jsonEvent = "[{\"event\":\"code_testrun\",\"id\":{\"method\":\"test[418: combination of <[Passed, Pending, Passed, Passed]>]\",\"build_number\":\"29\",\"class\":\"ActivityStatusCommutativeTest\",\"package\":\"com.hp.alm.platform.dataflow\",\"root_build_number\":\"29\"},\"tags\":{\"scm_branch\":null,\"build_result\":\"UNSTABLE\",\"flow_type\":null,\"build_uri_path\":\"job/ALM-Newton-Compile-Server/29/\",\"schema_version\":null},\"result\":{\"status\":\"PASSED\",\"skipped\":false,\"run_time\":0,\"failed_since\":0,\"age\":0,\"error\":null,\"skipped_message\":null},\"source\":{\"root_job_name\":\"ALM-Newton-Compile-Server\",\"build_server_uri\":\"http://mydtbld0048.isr.hp.com:8888/jenkins\",\"job_name\":\"ALM-Newton-Compile-Server\",\"source_type\":\"jenkins\",\"build_server_host\":\"mydtbld0048.isr.hp.com\"},\"time\":\"2015-08-20T10:37:41\"},{\"event\":\"issue_change\",\"time\":\"2015-07-27T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"id\":{\"uid\":\"1122\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"fields\":[{\"name\":\"Status\",\"from\":\"New\",\"to\":\"Open\",\"ttc\":124},{\"name\":\"Priority\",\"to\":\"2-Medium\"}],\"comments\":[{\"topic\":\"re: Problem to delpoy on AWS\",\"text\":\"larin fdsfsdf, fsdfds fsdfsfs\",\"time_since_last_post(h)\":12.5}]}]";

        List<BaseEvent> receivedEvents = null;
        receivedEvents = new ObjectMapper().readValue(jsonEvent, new TypeReference<List<BaseEvent>>(){});

        ElasticSearchHandler esh = new ElasticSearchHandler();

        byte[] output = esh.convert(receivedEvents.get(0));
        String outputString = new String(output, "UTF-8");
        assertEquals("root_job_name should be presented once", 1, StringUtils.countOccurrencesOf(outputString, "\"root_job_name\""));
        assertEquals("time field should be presented once", 1, StringUtils.countOccurrencesOf(outputString, "\"timestamp\":"));

        output = esh.convert(receivedEvents.get(1));
        outputString = new String(output, "UTF-8");
        assertEquals("Priority should be presented once", 1, StringUtils.countOccurrencesOf(outputString, "\"Priority\""));
        assertEquals("time field should be presented once", 1, StringUtils.countOccurrencesOf(outputString, "\"timestamp\":"));
    }
}
