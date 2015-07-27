package com.hp.gaia.mgs.dto.commit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.hp.gaia.mgs.dto.BaseEvent;
import com.hp.gaia.mgs.dto.change.TestChangeEvent;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by belozovs on 7/15/2015.
 */

public class CodeCommitEventParserTest {

    private final String codeCommit = "{\"event\":\"code_commit\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"repository\":\"git://github.com/hp/mqm-server\",\"branch\":\"master\"},\"id\":{\"uid\":\"8ad3535acb2a724eb0058fa071c788d48ab6978e\"},\"tags\":{\"user\":\"alex\"},\"files\":[{\"file\":\"README.md\",\"loc\":10},{\"file\":\" src/main/java/managers/RabbitmqManager.java\",\"loc\":-14}]}";

    @Test
    public void testCodeCommit() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        List<BaseEvent> events = mapper.readValue("[" + codeCommit + "]", new TypeReference<List<BaseEvent>>() {
        });

        assertEquals("Single event in the list", 1, events.size());
        assertEquals("Event of type code_commit", CodeCommitEvent.EVENT_TYPE, events.get(0).getType());
        assertEquals("Two files presented", 2, ((CodeCommitEvent) events.get(0)).getChangedFilesList().size());
        assertEquals("Three tags presented", 1, (events.get(0)).getTags().size());
        assertEquals("Two items in source presented", 2, (events.get(0)).getSource().size());
    }

}
