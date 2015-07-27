package com.hp.gaia.mgs.dto.change;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.BaseEvent;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by belozovs on 7/15/2015.
 */

public class IssueChangeEventParserTest {

    private final String issueChange = "{\"event\":\"issue_change\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"id\":{\"uid\":\"1122\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"fields\":[{\"name\":\"Status\",\"from\":\"New\",\"to\":\"Open\",\"ttc\":124},{\"name\":\"Priority\",\"to\":\"2-Medium\"}],\"comments\":[{\"topic\":\"re: Problem to delpoy on AWS\",\"text\":\"larin fdsfsdf, fsdfds fsdfsfs\",\"time_since_last_post(h)\":12.5}]}";

    @Test
    public void testIssueChange() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        List<BaseEvent> events = mapper.readValue("[" + issueChange + "]", new TypeReference<List<BaseEvent>>() {
        });

        assertEquals("Single event in the list", 1, events.size());
        assertEquals("Event of type issue_change", IssueChangeEvent.EVENT_TYPE, events.get(0).getType());
        assertEquals("Single comment presented", 1, ((IssueChangeEvent)events.get(0)).getComments().size());
        assertEquals("Two fields presented", 2, ((IssueChangeEvent)events.get(0)).getFields().size());
        assertEquals("Two tags presented", 2, (events.get(0)).getTags().size());
        assertEquals("Three items in source presented", 3, (events.get(0)).getSource().size());
    }

}
