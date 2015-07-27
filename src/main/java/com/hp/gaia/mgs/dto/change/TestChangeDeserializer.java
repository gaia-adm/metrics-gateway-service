package com.hp.gaia.mgs.dto.change;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.hp.gaia.mgs.dto.CommonDeserializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by belozovs on 7/15/2015.
 * IssueChange event deserializer
 */
public class TestChangeDeserializer extends JsonDeserializer<TestChangeEvent> implements CommonDeserializationUtils {

    Logger logger = LoggerFactory.getLogger(TestChangeDeserializer.class);

    @Override
    public TestChangeEvent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);

        TestChangeEvent tce = new TestChangeEvent();

        DeserializationUtils du = new DeserializationUtils();

        Iterator<JsonNode> fieldNodes = node.get("fields").elements();

        while (fieldNodes.hasNext()) {
            JsonNode fieldNode = fieldNodes.next();
            tce.addField(du.fetchIssueField(fieldNode));
        }

        fillObjectMap(tce.getId(), "id", node);
        fillStringMap(tce.getSource(), "source", node);
        fillStringMap(tce.getTags(), "tags", node);

        tce.setAttachments(du.fetchMultipleMaps(node, "attachments"));
        tce.setSteps(du.fetchMultipleMaps(node, "steps"));

        if (node.get("time") != null) {
            tce.setTime(javax.xml.bind.DatatypeConverter.parseDateTime(node.get("time").asText()).getTime());
        } else {
            tce.setTime(new Date());
        }

        return tce;
    }


}
