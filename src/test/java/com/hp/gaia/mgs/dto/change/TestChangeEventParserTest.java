package com.hp.gaia.mgs.dto.change;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.hp.gaia.mgs.dto.BaseEvent;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by belozovs on 7/15/2015.
 */

public class TestChangeEventParserTest {

    private final String testChange = "{\"time\":\"2015-07-27T23:00:00Z\",\"id\":{\"uid\":\"2341\"},\"event\":\"test_change\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"fields\":[{\"name\":\"State\",\"from\":\"Maintenance\",\"to\":\"Ready\",\"ttc(d)\":11}],\"steps\":[{\"new\":10,\"modified\":3,\"deleted\":1}],\"attachments\":[{\"name\":\"readme.docx\",\"size\":\"1.3M\"}]}";

    @Test
    public void testTestChange() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        List<BaseEvent> events = mapper.readValue("[" + testChange + "]", new TypeReference<List<BaseEvent>>() {
        });

        assertEquals("Single event in the list", 1, events.size());
        assertEquals("Event of type test_change", TestChangeEvent.EVENT_TYPE, events.get(0).getType());
        assertEquals("Single attachment presented", 1, ((TestChangeEvent) events.get(0)).getAttachments().size());
        assertEquals("Single field presented", 1, ((TestChangeEvent) events.get(0)).getFields().size());
        assertEquals("Two tags presented", 2, (events.get(0)).getTags().size());
        assertEquals("Three items in source presented", 3, (events.get(0)).getSource().size());
        assertEquals("Three steps where modified", 3, ((IntNode) ((TestChangeEvent) events.get(0)).getSteps().get(0).get("modified")).asInt());
    }

}
