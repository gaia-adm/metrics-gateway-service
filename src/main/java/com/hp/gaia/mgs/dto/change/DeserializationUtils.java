package com.hp.gaia.mgs.dto.change;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by belozovs on 7/27/2015.
 *
 * Some methods that are reusable in this package.
 * NOTE: the class access level is not public
 */
class DeserializationUtils {

    Logger logger = LoggerFactory.getLogger(DeserializationUtils.class);

    InnerField fetchIssueField(JsonNode fieldNode) {
        Iterator<String> subfieldNames = fieldNode.fieldNames();
        JsonNode fieldNameNode = fieldNode.get("name");
        if (fieldNameNode == null) {
            return null;
        }
        InnerField innerField = new InnerField(fieldNameNode.asText());

        while (subfieldNames.hasNext()) {
            String curSubFieldName = subfieldNames.next();
            switch (curSubFieldName.toLowerCase()) {
                case "name":
                    break;
                case "to":
                    innerField.setTo(fieldNode.get(curSubFieldName).asText());
                    break;
                case "from":
                    innerField.setFrom(fieldNode.get(curSubFieldName).asText());
                    break;
                case "ttc":
                    innerField.setTtc(fieldNode.get(curSubFieldName).asLong());
                    break;
                default:
                    innerField.addCustomField(curSubFieldName, fieldNode.get(curSubFieldName).asText());
                    break;
            }
        }

        if (StringUtils.isEmpty(innerField.getName()) || innerField.getTo() == null) {
            logger.error("Empty or missing data provided for field 'name' or 'to' attributes: {}", fieldNode);
            throw new RuntimeException("Incomplete data provided: 'name' and 'to' attributes are mandatory for all fields; 'name' must not be empty also");
        }

        return innerField;
    }

    List<Map<String, Object>> fetchMultipleMaps(JsonNode node, String name) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (node.get(name) != null) {
            Iterator<JsonNode> nodesIterator = node.get(name).elements();
            while (nodesIterator.hasNext()) {
                JsonNode nextNode = nodesIterator.next();
                Map<String, Object> map = new HashMap<>();
                Iterator<String> fieldNames = nextNode.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    map.put(fieldName, nextNode.get(fieldName));
                }
                if(!map.isEmpty()){
                    list.add(map);
                }
            }
        }
        return list;
    }
}
