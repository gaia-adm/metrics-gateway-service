package com.hp.gaia.mgs.dto.issuechange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.issuechange.IssueChangeEvent;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by belozovs on 7/15/2015.
 */

public class IssueChangeEventParserTest {

    private final String ISSUE_CHANGE_EVENT = "{\"event\":\"issue_change\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"id\":{\"uid\":\"1122\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"fields\":[{\"name\":\"Status\",\"from\":\"New\",\"to\":\"Open\",\"ttc\":124},{\"name\":\"Priority\",\"to\":\"2-Medium\"}]}";

    private ObjectMapper mapper;


    @Before
    public void setUp() throws Exception {

        mapper = new ObjectMapper();

    }

    @Test
    public void test() throws IOException {
        IssueChangeEvent result = mapper.readValue(ISSUE_CHANGE_EVENT, IssueChangeEvent.class);
        System.out.println("done!");
    }

}
