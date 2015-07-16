package com.hp.gaia.mgs.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.issuechange.IssueChangeEvent;
import com.hp.gaia.mgs.dto.testrun.AlmTestRunEvent;
import com.hp.gaia.mgs.dto.testrun.CodeTestRunEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * Created by belozovs on 7/16/2015.
 */
public class MainEventParserTest {

    private static String EVENT_SOURCE_OUTSIDE = "{\"event\":\"issue_change\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"points\":[{\"time\":\"2015-11-10T23:00:00Z\",\"id\":{\"uid\":\"1122\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"fields\":[{\"name\":\"Status\",\"from\":\"New\",\"to\":\"Open\",\"ttc\":124},{\"name\":\"Priority\",\"to\":\"2-Medium\",\"newfield\":\"my new field\"}]}]}";
    private static String EVENT_ALL_COMMON_OUTSIDE = "{\"event\":\"issue_change\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"id\":{\"uid\":\"1122\"},\"points\":[{\"fields\":[{\"name\":\"Status\",\"from\":\"New\",\"to\":\"Open\",\"ttc\":124},{\"name\":\"Priority\",\"to\":\"2-Medium\",\"newfield\":\"my new field\"}]}]}";
    private static String EVENT_ALL_COMMON_OUTSIDE_MULTI = "{\"event\":\"issue_change\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"id\":{\"uid\":\"1122\"},\"points\":[{\"fields\":[{\"name\":\"Status\",\"from\":\"New\",\"to\":\"Open\",\"ttc\":124},{\"name\":\"Priority\",\"to\":\"2-Medium\",\"newfield\":\"my new field\"}]},{\"fields\":[{\"name\":\"Owner\",\"to\":\"Obama\"},{\"name\":\"Severity\",\"to\":\"289-Extremely Sever\",\"isRealyExtrim\":true}]}]}";
    private static String EVENT_ALL_COMMON_OUTSIDE_MULTI_MULTITYPES = "{\"event\":\"issue_change\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"id\":{\"uid\":\"1122\"},\"points\":[{\"fields\":[{\"name\":\"Status\",\"from\":\"New\",\"to\":\"Open\",\"ttc\":124},{\"name\":\"Priority\",\"to\":\"2-Medium\",\"newfield\":\"my new field\"}]},{\"fields\":[{\"name\":\"Owner\",\"to\":\"Obama\"},{\"name\":\"Severity\",\"to\":\"289-Extremely Sever\",\"isRealyExtrim\":true}]},{\"event\":\"tm_testrun\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"id\":{\"instance\":\"245\",\"test_id\":\"34\"},\"tags\":{\"workspace\":\"CRM\",\"testset\":\"Regression\",\"user\":\"john\",\"type\":\"Manual\"},\"result\":{\"status\":\"Passed\",\"run_time\":380,\"steps\":[{\"name\":\"step 1\",\"status\":\"Passed\",\"runt_time\":12},{\"name\":\"step 2\",\"status\":\"Skipped\"},{\"name\":\"step 3\",\"status\":\"Passed\",\"runt_time\":368}]}},{\"event\":\"code_testrun\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"repository\":\"git://github.com/hp/mqm-server\",\"branch\":\"master\"},\"id\":{\"package\":\"com.hp.mqm\",\"class\":\"FilterBuilder\",\"method\":\"TestLogicalOperators\"},\"tags\":{\"build_job\":\"backend_job\",\"browser\":\"firefox\",\"build_label\":\"1.7.0\"},\"result\":{\"status\":\"error\",\"error\":\"NullPointerException: ...\",\"setup_time\":35,\"tear_down_time\":20,\"run_time\":130}}]}";

    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
    }

    @Test
    public void test_EVENT_SOURCE_OUTSIDE() throws Exception {

        List<BaseEvent> result = (List<BaseEvent>) mapper.readValue(EVENT_SOURCE_OUTSIDE, BaseEvent.class);

        System.out.println("done!");
    }

    @Test
    public void test_EVENT_ALL_COMMON_OUTSIDE() throws Exception {

        List<BaseEvent> result = (List<BaseEvent>) mapper.readValue(EVENT_ALL_COMMON_OUTSIDE, BaseEvent.class);

        System.out.println("done!");
    }

    @Test
    public void test_EVENT_ALL_COMMON_OUTSIDE_MULTI() throws Exception {

        List<BaseEvent> result = (List<BaseEvent>) mapper.readValue(EVENT_ALL_COMMON_OUTSIDE_MULTI, BaseEvent.class);

        System.out.println("done!");
    }


    @Test
    public void test_EVENT_ALL_COMMON_OUTSIDE_MULTI_MULTITYPES() throws Exception {
        List<BaseEvent> result = (List<BaseEvent>) mapper.readValue(EVENT_ALL_COMMON_OUTSIDE_MULTI_MULTITYPES, BaseEvent.class);

        assertEquals("Four events expected", 4, result.size());
        Map<String, Integer> eventTypes = new HashMap<>();
        for(BaseEvent event : result){
            if(eventTypes.get(event.getType()) == null){
                eventTypes.put(event.getType(), 1);
            } else {
                eventTypes.put(event.getType(), eventTypes.get(event.getType()) + 1);
            }
        }
        assertEquals("Two issue changes expected", 2, eventTypes.get(IssueChangeEvent.EVENT_TYPE).longValue());
        assertEquals("One ALM test run expected", 1, eventTypes.get(AlmTestRunEvent.EVENT_TYPE).longValue());
        assertEquals("One code test run expected", 1, eventTypes.get(CodeTestRunEvent.EVENT_TYPE).longValue());

        System.out.println("done!");
    }
}
