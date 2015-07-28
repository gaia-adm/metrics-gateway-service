package com.hp.gaia.mgs.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.change.IssueChangeEvent;
import com.hp.gaia.mgs.dto.commit.CodeCommitEvent;
import com.hp.gaia.mgs.dto.testrun.AlmTestRunEvent;
import com.hp.gaia.mgs.dto.testrun.CodeTestRunEvent;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by belozovs on 7/16/2015.
 *
 * Utilities methods that can be useful for multiple types deserialization
 */


public interface CommonDeserializationUtils {

    /**
     * Fill a map with data from mapType element of json node
     */

    default int fillStringMap(Map<String, String> map, String mapType, JsonNode node){

        if(node.get(mapType) != null) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.get(mapType).fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                map.put(field.getKey(), field.getValue().asText());
            }
        }
        return map.size();
    }


    default int fillObjectMap(Map<String, Object> map, String mapType, JsonNode node){

        if(node.get(mapType) != null) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.get(mapType).fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                switch (field.getValue().getClass().getName()){
                    case "com.fasterxml.jackson.databind.node.BooleanNode":
                        map.put(field.getKey(), field.getValue().asBoolean());
                        break;
                    case "com.fasterxml.jackson.databind.node.IntNode":
                        map.put(field.getKey(), field.getValue().asInt());
                        break;
                    case "com.fasterxml.jackson.databind.node.LongNode":
                        map.put(field.getKey(), field.getValue().asLong());
                        break;
                    case "com.fasterxml.jackson.databind.node.DoubleNode":
                        map.put(field.getKey(), field.getValue().asDouble());
                        break;
                    case "com.fasterxml.jackson.databind.node.FloatNode":
                        map.put(field.getKey(), field.getValue().asDouble());
                        break;
                    case "com.fasterxml.jackson.databind.node.TextNode":
                        map.put(field.getKey(), field.getValue().asText());
                        break;
                    default :
                        map.put(field.getKey(), field.getValue().asText());
                        break;
                }

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
            case CodeCommitEvent.EVENT_TYPE:
                nextEvent = new ObjectMapper().readValue(point.toString(), CodeCommitEvent.class);
                break;
            default:
                System.out.println("No valid event type found: " + pointType);
                throw new RuntimeException("No valid event type provided: " + pointType);
        }
        return nextEvent;
    }

    default Date generateDate(JsonNode node){
        TimestampRandomizer.getInstance().nextNumber();
        return javax.xml.bind.DatatypeConverter.parseDateTime(node.get("time").asText()).getTime();
    }
}
