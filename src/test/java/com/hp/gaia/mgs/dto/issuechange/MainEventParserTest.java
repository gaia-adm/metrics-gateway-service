package com.hp.gaia.mgs.dto.issuechange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.BaseEvent;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 * Created by belozovs on 7/16/2015.
 */
public class MainEventParserTest {

    private static String EVENT_SOURCE_OUTSIDE = "{\"event\":\"issue_change\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"points\":[{\"time\":\"2015-11-10T23:00:00Z\",\"id\":{\"uid\":\"1122\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"fields\":[{\"name\":\"Status\",\"from\":\"New\",\"to\":\"Open\",\"ttc\":124},{\"name\":\"Priority\",\"to\":\"2-Medium\",\"newfield\":\"my new field\"}]}]}";
    private static String EVENT_ALL_COMMON_OUTSIDE = "{\"event\":\"issue_change\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"id\":{\"uid\":\"1122\"},\"points\":[{\"fields\":[{\"name\":\"Status\",\"from\":\"New\",\"to\":\"Open\",\"ttc\":124},{\"name\":\"Priority\",\"to\":\"2-Medium\",\"newfield\":\"my new field\"}]}]}";
    private static String EVENT_ALL_COMMON_OUTSIDE_MULTI = "{\"event\":\"issue_change\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"id\":{\"uid\":\"1122\"},\"points\":[{\"fields\":[{\"name\":\"Status\",\"from\":\"New\",\"to\":\"Open\",\"ttc\":124},{\"name\":\"Priority\",\"to\":\"2-Medium\",\"newfield\":\"my new field\"}]},{\"fields\":[{\"name\":\"Owner\",\"to\":\"Obama\"},{\"name\":\"Severity\",\"to\":\"289-Extremely Sever\",\"isRealyExtrim\":true}]}]}";

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

}
