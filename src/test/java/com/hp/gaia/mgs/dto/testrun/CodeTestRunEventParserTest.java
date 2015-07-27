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

    private final String codeTestrun = "{\"event\":\"code_testrun\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"repository\":\"git://github.com/hp/mqm-server\",\"branch\":\"master\"},\"id\":{\"package\":\"com.hp.mqm\",\"class\":\"FilterBuilder\",\"method\":\"TestLogicalOperators\"},\"tags\":{\"build_job\":\"backend_job\",\"browser\":\"firefox\",\"build_label\":\"1.7.0\"},\"result\":{\"status\":\"error\",\"error\":\"NullPointerException: ...\",\"setup_time\":35,\"tear_down_time\":20,\"run_time\":130}}";

    @Test
    public void testCodeTestrun() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        List<BaseEvent> events = mapper.readValue("[" + codeTestrun + "]", new TypeReference<List<BaseEvent>>() {
        });

        assertEquals("Single event in the list", 1, events.size());
        assertEquals("Event of type code_testrun", CodeTestRunEvent.EVENT_TYPE, events.get(0).getType());
        assertEquals("Three tags presented", 3, (events.get(0)).getTags().size());
        assertEquals("Two items in source presented", 2, (events.get(0)).getSource().size());
        assertEquals("Five items in result presented", 5, ((CodeTestRunEvent) (events.get(0))).getResult().getMembersAsMap().size());
    }

}
