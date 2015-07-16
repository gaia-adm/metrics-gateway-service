package com.hp.gaia.mgs.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by belozovs on 7/16/2015.
 */

public interface CommonDeserializerUtils {

    public default int fillMap(Map<String, String> map, String mapType, JsonNode node){

        if(node.get(mapType) != null) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.get(mapType).fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                map.put(field.getKey(), field.getValue().asText());
            }
        }

        return map.size();
    }

}
