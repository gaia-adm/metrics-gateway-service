package com.hp.gaia.mgs.dto.issuechange;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.hp.gaia.mgs.dto.CommonDeserializerUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by belozovs on 7/15/2015.
 */
public class IssueChangeDeserializer extends JsonDeserializer<IssueChangeEvent> implements CommonDeserializerUtils {

    private static String FIELD_NAME_NAME = "name";

    @Override
    public IssueChangeEvent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);

        IssueChangeEvent ice = new IssueChangeEvent();

        Iterator<JsonNode> fieldNodes = node.get("fields").elements();

        while (fieldNodes.hasNext()) {
            JsonNode fieldNode = fieldNodes.next();
            ice.addField(fetchIssueField(fieldNode));
        }

        fillMap(ice.getId(), "id", node);
        fillMap(ice.getSource(), "source", node);
        fillMap(ice.getTags(), "tags", node);


        if (node.get("time") != null) {
            ice.setTime(javax.xml.bind.DatatypeConverter.parseDateTime(node.get("time").asText()).getTime());
        } else {
            ice.setTime(new Date());
        }

        return ice;
    }

    private IssueField fetchIssueField(JsonNode fieldNode) {
        Iterator<String> subfieldNames = fieldNode.fieldNames();
        JsonNode fieldNameNode = fieldNode.get(FIELD_NAME_NAME);
        if (fieldNameNode == null) {
            return null;
        }
        IssueField issueField = new IssueField(fieldNameNode.asText());

        while (subfieldNames.hasNext()) {
            String curSubFieldName = subfieldNames.next();
            switch (curSubFieldName.toLowerCase()) {
                case "to":
                    issueField.setTo(fieldNode.get(curSubFieldName).asText());
                    break;
                case "from":
                    issueField.setFrom(fieldNode.get(curSubFieldName).asText());
                    break;
                case "ttc":
                    issueField.setTtc(fieldNode.get(curSubFieldName).asText());
                    break;
                default:
                    if(!curSubFieldName.equalsIgnoreCase(FIELD_NAME_NAME)) {
                        issueField.addCustomField(curSubFieldName, fieldNode.get(curSubFieldName).asText());
                    }
                    break;
            }
        }

        return issueField;
    }

}
