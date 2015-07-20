package com.hp.gaia.mgs.dto.issuechange;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.hp.gaia.mgs.dto.CommonDeserializerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by belozovs on 7/15/2015.
 * IssueChange event deserializer
 */
public class IssueChangeDeserializer extends JsonDeserializer<IssueChangeEvent> implements CommonDeserializerUtils {

    Logger logger = LoggerFactory.getLogger(IssueChangeDeserializer.class);

    @Override
    public IssueChangeEvent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

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
        JsonNode fieldNameNode = fieldNode.get("name");
        if (fieldNameNode == null) {
            return null;
        }
        IssueField issueField = new IssueField(fieldNameNode.asText());

        while (subfieldNames.hasNext()) {
            String curSubFieldName = subfieldNames.next();
            switch (curSubFieldName.toLowerCase()) {
                case "name":
                    break;
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
                    issueField.addCustomField(curSubFieldName, fieldNode.get(curSubFieldName).asText());
                    break;
            }
        }

        if(StringUtils.isEmpty(issueField.getName()) || StringUtils.isEmpty(issueField.getTo())){
            logger.error("Empty data provided for field 'name' or 'to' attribute: {}", fieldNode);
            throw new RuntimeException("Incomplete data provided: 'name' and 'to' attributes are mandatory for all fields");
        }

        return issueField;
    }

}
