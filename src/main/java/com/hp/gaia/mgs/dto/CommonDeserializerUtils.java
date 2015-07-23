package com.hp.gaia.mgs.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.change.IssueChangeEvent;
import com.hp.gaia.mgs.dto.testrun.AlmTestRunEvent;
import com.hp.gaia.mgs.dto.testrun.CodeTestRunEvent;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by belozovs on 7/16/2015.
 *
 * Utilities methods that can be useful for multiple types deserialization
 */


public interface CommonDeserializerUtils {

    /**
     * Fill a map with data from mapType element of json node
     */
    default int fillMap(Map<String, String> map, String mapType, JsonNode node){

        if(node.get(mapType) != null) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.get(mapType).fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                map.put(field.getKey(), field.getValue().asText());
            }
        }
        return map.size();
    }


    /**
     * Deserialize event with custom deserializer
     * Custom deserializer is selected based on the event type its type
     * If no valid event type found, exception thrown
     *
     * @param point json for deserialization
     * @param pointType type to use for deserialization
     * @return event of one of the types extending BaseEvent
     * @throws IOException in case of deserialization problem
     */
    default BaseEvent deserializeEvent(JsonNode point, String pointType) throws IOException {

        BaseEvent nextEvent;
        switch (pointType) {
            case IssueChangeEvent.EVENT_TYPE:
                nextEvent = new ObjectMapper().readValue(point.toString(), IssueChangeEvent.class);
                break;
            case AlmTestRunEvent.EVENT_TYPE:
                nextEvent = new ObjectMapper().readValue(point.toString(), AlmTestRunEvent.class);
                break;
            case CodeTestRunEvent.EVENT_TYPE:
                nextEvent = new ObjectMapper().readValue(point.toString(), CodeTestRunEvent.class);
                break;
            default:
                System.out.println("No valid event type found: " + pointType);
                throw new RuntimeException("No valid event type provided: " + pointType);
        }
        return nextEvent;
    }

}
