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

public class AlmTestRunEventParserTest {

    private final String almTestrun = "{\"event\":\"tm_testrun\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"id\":{\"instance\":\"245\",\"test_id\":\"34\"},\"tags\":{\"workspace\":\"CRM\",\"testset\":\"Regression\",\"user\":\"john\",\"type\":\"Manual\"},\"result\":{\"status\":\"Passed\",\"run_time\":380,\"steps\":[{\"name\":\"step 1\",\"status\":\"Passed\",\"runt_time\":12},{\"name\":\"step 2\",\"status\":\"Skipped\"},{\"name\":\"step 3\",\"status\":\"Passed\",\"runt_time\":368}]}}";

    @Test
    public void testAlmTestrun() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        List<BaseEvent> events = mapper.readValue("[" + almTestrun + "]", new TypeReference<List<BaseEvent>>() {
        });

        assertEquals("Single event in the list", 1, events.size());
        assertEquals("Event of type tm_testrun", AlmTestRunEvent.EVENT_TYPE, events.get(0).getType());
        assertEquals("Four tags presented", 4, (events.get(0)).getTags().size());
        assertEquals("Three items in source presented", 3, (events.get(0)).getSource().size());
        assertEquals("Three items in result (not including steps) presented", 3, ((AlmTestRunEvent) (events.get(0))).getResult().getMembersAsMap().size());
        assertEquals("Three steps in result presented", 3, ((AlmTestRunEvent) (events.get(0))).getResult().getSteps().size());
    }
}
