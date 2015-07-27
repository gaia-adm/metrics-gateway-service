package com.hp.gaia.mgs.dto.testrun;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.hp.gaia.mgs.dto.CommonDeserializationUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by belozovs on 7/27/2015.
 */
public class AlmTestRunDeserializer extends StdDeserializer<AlmTestRunEvent> implements CommonDeserializationUtils {

    public AlmTestRunDeserializer() {
        super(AlmTestRunEvent.class);
    }

    @Override
    public AlmTestRunEvent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);

        AlmTestRunEvent atre = new AlmTestRunEvent();

        DeserializationUtils du = new DeserializationUtils();

        fillObjectMap(atre.getId(), "id", node);
        fillStringMap(atre.getSource(), "source", node);
        fillStringMap(atre.getTags(), "tags", node);

        atre.setResult(du.getAlmTestRunResult(node));

        if (node.get("time") != null) {
            atre.setTime(javax.xml.bind.DatatypeConverter.parseDateTime(node.get("time").asText()).getTime());
        } else {
            atre.setTime(new Date());
        }

        return atre;
    }

}
