package com.hp.gaia.mgs.dto.testrun;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import jersey.repackaged.com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by belozovs on 7/27/2015.
 */
class DeserializationUtils {

    TestRunResult getCodeTestRunResult(JsonNode runNode) {
        JsonNode resultNode = runNode.get("result");
        if (resultNode == null) {
            return null;
        } else {
            TestRunResult trr = new TestRunResult();
            Iterator<String> fieldNames = resultNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                switch (fieldName) {
                    case "status":
                        trr.setStatus(getStringValue(resultNode, fieldName));
                        break;
                    case "error":
                        trr.setErrorString(getStringValue(resultNode, fieldName));
                        break;
                    case "run_time":
                        trr.setRunTime(resultNode.get(fieldName).asLong());
                        break;
                    default:
                        if(resultNode.get(fieldName) != null) {
                            trr.getCustomFields().put(fieldName, resultNode.get(fieldName));
                        }
                        break;
                }
            }
            return trr;
        }
    }

    static String getStringValue(JsonNode objectNode, String fieldName) {
        JsonNode jsonNode = objectNode.get(fieldName);
        if (jsonNode instanceof TextNode) {
            return jsonNode.asText();
        } else if (jsonNode instanceof NullNode || jsonNode == null) {
            return null;
        } else {
            throw new IllegalStateException("Unexpected value type for field '" + fieldName + "'");
        }
    }

    AlmTestRunResult getAlmTestRunResult(JsonNode runNode) {

        TestRunResult trr = getCodeTestRunResult(runNode);
        AlmTestRunResult atrr = new AlmTestRunResult();
        atrr.setStatus(trr.getStatus());
        atrr.setRunTime(trr.getRunTime());
        atrr.setErrorString(trr.getErrorString());
        atrr.setCustomFields(trr.getCustomFields());
        //remove steps if presented, it is handled differently for AlmTestRunResult, no need to duplicate this data
        atrr.getCustomFields().remove("steps");

        if (runNode.get("result") != null) {
            List<JsonNode> list = Lists.newArrayList(runNode.get("result").get("steps").iterator());
            for (JsonNode node : list) {
                atrr.addStep(new ObjectMapper().convertValue(node, new TypeReference<Map<String, Object>>() {
                }));
            }
        }
        return atrr;
    }

}
