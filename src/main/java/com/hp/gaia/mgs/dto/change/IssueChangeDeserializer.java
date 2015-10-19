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
import java.util.Map;

/**
 * Created by belozovs on 7/15/2015.
 * IssueChange event deserializer
 */
public class IssueChangeDeserializer extends JsonDeserializer<IssueChangeEvent> implements CommonDeserializationUtils {

    Logger logger = LoggerFactory.getLogger(IssueChangeDeserializer.class);

    @Override
    public IssueChangeEvent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);

        IssueChangeEvent ice = new IssueChangeEvent();

        DeserializationUtils du = new DeserializationUtils();

        fillObjectMap(ice.getId(), "id", node);
        fillStringMap(ice.getSource(), "source", node);
        fillStringMap(ice.getTags(), "tags", node);

        if(node.get("fields") == null) {
            Map<String, Object> idMap = ice.getId();
            StringBuffer sbId = new StringBuffer();
            for(String key : idMap.keySet()) {
                sbId.append(key).append(": ").append(idMap.get(key)).append(" ");
            }
            logger.warn("Empty fields section - ignoring event " + sbId.toString());
            return null;
        } else {
            Iterator<JsonNode> fieldNodes = node.get("fields").elements();
            while (fieldNodes.hasNext()) {
                JsonNode fieldNode = fieldNodes.next();
                ice.addField(du.fetchIssueField(fieldNode));
            }
        }


        ice.setComments(du.fetchMultipleMaps(node, "comments"));

        if (node.get("time") != null) {
            ice.setTime(javax.xml.bind.DatatypeConverter.parseDateTime(node.get("time").asText()).getTime());
        } else {
            ice.setTime(new Date());
        }

        return ice;
    }

}
