package com.hp.gaia.mgs.dto.testrun;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by belozovs on 7/16/2015.
 */
public class TestRunEventParserTest {

    private final static String CODE_TEST_RUN_EVENT = "{\"event\":\"code_testrun\",\"time\":\"2015-11-10T22:00:00Z\",\"source\":{\"repository\":\"git://github.com/hp/mqm-server\",\"branch\":\"master\"},\"id\":{\"package\":\"com.hp.mqm\",\"class\":\"FilterBuilder\",\"method\":\"TestLogicalOperators\"},\"tags\":{\"build_job\":\"backend_job\",\"browser\":\"firefox\",\"build_label\":\"1.7.0\"},\"result\":{\"status\":\"error\",\"error\":\"NullPointerException: ...\",\"setup_time\":35,\"tear_down_time\":20,\"run_time\":130}}";
    private final static String ALM_TEST_RUN_EVENT = "{\"event\":\"tm_testrun\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"id\":{\"instance\":\"245\",\"test_id\":\"34\"},\"tags\":{\"workspace\":\"CRM\",\"testset\":\"Regression\",\"user\":\"john\",\"type\":\"Manual\"},\"result\":{\"status\":\"Passed\",\"run_time\":380,\"steps\":[{\"name\":\"step 1\",\"status\":\"Passed\",\"runt_time\":12},{\"name\":\"step 2\",\"status\":\"Skipped\"},{\"name\":\"step 3\",\"status\":\"Passed\",\"runt_time\":368}]}}";
    private ObjectMapper mapper;


    @Before
    public void setUp() throws Exception {

        mapper = new ObjectMapper();

    }

    @Test
    public void testCodeTestRunEvent() throws IOException {
        CodeTestRunEvent result = mapper.readValue(CODE_TEST_RUN_EVENT, CodeTestRunEvent.class);
        System.out.println("done!");
    }

    @Test
    public void testAlmTestRunEvent() throws IOException {
        AlmTestRunEvent result = mapper.readValue(ALM_TEST_RUN_EVENT, AlmTestRunEvent.class);
        System.out.println("done!");
    }

}
