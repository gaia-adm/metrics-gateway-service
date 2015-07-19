package com.hp.gaia.mgs.dto.testrun;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.hp.gaia.mgs.dto.CommonDeserializerUtils;
import jersey.repackaged.com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by belozovs on 7/15/2015.
 * Deserializer for all types of TestRunEvent
 */
public class TestRunDeserializer extends StdDeserializer<TestRunEvent> implements CommonDeserializerUtils {

    public TestRunDeserializer() {
        super(TestRunEvent.class);
    }

    @Override
    public TestRunEvent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);

        Class<? extends TestRunEvent> trec = null;
        if (node.findValue("event") != null) {
            switch (node.findValue("event").asText()) {
                case AlmTestRunEvent.EVENT_TYPE:
                    trec = AlmTestRunEvent.class;
                    break;
                case CodeTestRunEvent.EVENT_TYPE:
                    trec = CodeTestRunEvent.class;
                    break;
            }
        }
        if (trec == null) {
            return null;
        }
        TestRunEvent tre;
        try {
            tre = trec.newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }

        fillMap(tre.getId(), "id", node);
        fillMap(tre.getSource(), "source", node);
        fillMap(tre.getTags(), "tags", node);

        tre.setType(node.findValue("event").asText());

        if (node.get("time") != null) {
            tre.setTime(javax.xml.bind.DatatypeConverter.parseDateTime(node.get("time").asText()).getTime());
        } else {
            tre.setTime(new Date());
        }

        tre.setResult(fetchTestRunResult(node));

        return tre;

    }

    private AlmTestRunResult fetchTestRunResult(JsonNode runNode) {
        JsonNode resultNode = runNode.get("result");
        if (resultNode == null) {
            return null;
        } else {
            AlmTestRunResult atrr = new AlmTestRunResult();
            Iterator<String> fieldNames = resultNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                switch (fieldName) {
                    case "status":
                        atrr.setStatus(resultNode.get(fieldName).asText());
                        break;
                    case "error":
                        atrr.setErrorString(resultNode.get(fieldName).asText());
                        break;
                    case "run_time":
                        atrr.setRunTime(resultNode.get(fieldName).asLong());
                        break;
                    case "steps":
                        List<JsonNode> list = Lists.newArrayList(resultNode.get("steps").iterator());
                        for (JsonNode node : list) {
                            atrr.addStep(new ObjectMapper().convertValue(node, new TypeReference<Map<String, Object>>() {
                            }));
                        }
                        break;
                    default:
                        atrr.getCustomFields().put(fieldName, resultNode.get(fieldName).asText());
                        break;
                }
            }
            return atrr;
        }
    }

}
