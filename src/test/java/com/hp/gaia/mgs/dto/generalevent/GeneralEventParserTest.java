package com.hp.gaia.mgs.dto.generalevent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.BaseEvent;
import com.hp.gaia.mgs.dto.commit.CodeCommitEvent;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by belozovs on 7/15/2015.
 */

public class GeneralEventParserTest {

    private final String generalEvent = "{\"event\":\"general\",\"time\":\"2015-07-27T23:00:00Z\",\"source\":{\"origin\":\"notyourbusiness\"},\"id\":{\"uid\":\"12345\"},\"tags\":{\"tag1\":\"foo\",\"tag2\":\"boo\"},\"data\":{\"field1\":\"value1\",\"field2\":\"value2\",\"field3\":3}}";

    @Test
    public void testCodeCommit() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        List<BaseEvent> events = mapper.readValue("[" + generalEvent + "]", new TypeReference<List<BaseEvent>>() {
        });

        assertEquals("Single event in the list", 1, events.size());
        assertEquals("Event of type general", GeneralEvent.EVENT_TYPE, events.get(0).getType());
        assertEquals("Single item in source presented", 1, events.get(0).getSource().size());
        assertEquals("Single item in id presented", 1, events.get(0).getId().size());
        assertEquals("Two tags presented", 2, (events.get(0)).getTags().size());
        assertEquals("Three data fields presented ", 3, ((GeneralEvent)(events.get(0))).getData().size());
        assertEquals("Field1 value is value1", "value1", ((GeneralEvent)(events.get(0))).getData().get("field1"));
        assertEquals("Field3 value is 3 (integer)", 3, ((GeneralEvent) (events.get(0))).getData().get("field3"));
    }

}
