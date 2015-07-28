package com.hp.gaia.mgs.dto.generalevent;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.hp.gaia.mgs.dto.CommonDeserializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Created by belozovs on 7/28/2015.
 */
public class GeneralEventDeserializer extends JsonDeserializer<GeneralEvent>  implements CommonDeserializationUtils {

    Logger logger = LoggerFactory.getLogger(GeneralEventDeserializer.class);

    @Override
    public GeneralEvent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);

        GeneralEvent ge = new GeneralEvent();

        fillObjectMap(ge.getId(), "id", node);
        fillStringMap(ge.getSource(), "source", node);
        fillStringMap(ge.getTags(), "tags", node);

        fillObjectMap(ge.getData(),"data", node);

        if (node.get("time") != null) {
            ge.setTime(javax.xml.bind.DatatypeConverter.parseDateTime(node.get("time").asText()).getTime());
        } else {
            ge.setTime(new Date());
        }

        return ge;
    }
}
