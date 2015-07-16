package com.hp.gaia.mgs.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.issuechange.IssueChangeEvent;
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
    public List<BaseEvent> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);
        BaseEvent commonPart = new BaseEvent();
        List<BaseEvent> events = new ArrayList<>();

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

        List<JsonNode> points = Lists.newArrayList(node.get("points"));

        for(int i = 0; i< points.size(); i++) {

            String pointType = (points.get(i).get("event") != null) ? points.get(i).get("event").asText() : commonPart.getType();
            switch (pointType) {
                case IssueChangeEvent.EVENT_TYPE:
                    IssueChangeEvent ice = new ObjectMapper().readValue(points.get(i).toString(), IssueChangeEvent.class);
                    if(ice.getSource().isEmpty()){
                        ice.setSource(commonPart.getSource());
                    }
                    if(ice.getTags().isEmpty()){
                        ice.setTags(commonPart.getTags());
                    }
                    if(ice.getId().isEmpty()){
                        ice.setId(commonPart.getId());
                    }
                    if(ice.getTime() == null){
                        ice.setTime(commonPart.getTime());
                    }
                    if(ice.getType() == null){
                        ice.setType(pointType);
                    }
                    events.add(ice);
                    break;
                default:
                    System.out.println("No valid event type found: " + pointType);
                    break;
            }

        }

        return events;
    }


}
