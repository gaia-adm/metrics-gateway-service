package com.hp.gaia.mgs.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import jersey.repackaged.com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by belozovs on 7/16/2015.
 *
 * Strict common/point level separation - if common field presented, it is ignored on point level EXCEPTING event type
 *
 */
public class MainEventDeserializer extends JsonDeserializer<List<BaseEvent>> implements CommonDeserializerUtils {

    @Override
    public List<BaseEvent> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);
        BaseEvent commonPart = new BaseEvent();
        List<BaseEvent> events = new ArrayList<>();

        //get points for further usage or fail immediately, if no points provided
        List<JsonNode> points = Lists.newArrayList(node.get("points"));
        if(points == null || points.isEmpty()){
            throw new RuntimeException("No events provided");
        }

        if(node.get("id") != null){
            fillMap(commonPart.getId(), "id", node);
        }
        if(node.get("source") != null){
            fillMap(commonPart.getSource(), "source", node);
        }
        if(node.get("tags") != null){
            fillMap(commonPart.getTags(), "tags", node);
        }
        if(node.get("time") != null) {
            commonPart.setTime(javax.xml.bind.DatatypeConverter.parseDateTime(node.get("time").asText()).getTime());
        }
        if(node.get("event") != null){
            commonPart.setType(node.get("event").asText());
        }

        for (JsonNode point : points) {

            //for each point, the event type can be different, so we should check it
            String pointType = (point.get("event") != null) ? point.get("event").asText() : commonPart.getType();
            //deserialization
            BaseEvent nextEvent = deserializeEvent(point, pointType);
            //Enrich event with "common" data (common for all events in the request), if relevant section inside the event is empty
            if (nextEvent != null) {
                if (nextEvent.getSource().isEmpty()) {
                    nextEvent.setSource(commonPart.getSource());
                }
                if (nextEvent.getTags().isEmpty()) {
                    nextEvent.setTags(commonPart.getTags());
                }
                if (nextEvent.getId().isEmpty()) {
                    nextEvent.setId(commonPart.getId());
                }
                if (nextEvent.getTime() == null) {
                    nextEvent.setTime(commonPart.getTime());
                }
                if (nextEvent.getType() == null) {
                    nextEvent.setType(pointType);
                }
                events.add(nextEvent);
            }

        }

        return events;
    }


}
