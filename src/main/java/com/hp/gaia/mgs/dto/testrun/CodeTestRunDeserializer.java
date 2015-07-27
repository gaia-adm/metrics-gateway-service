package com.hp.gaia.mgs.dto.testrun;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.hp.gaia.mgs.dto.CommonDeserializationUtils;

import java.io.IOException;
import java.util.Date;

/**
 * Created by belozovs on 7/27/2015.
 */
public class CodeTestRunDeserializer extends StdDeserializer<CodeTestRunEvent> implements CommonDeserializationUtils {

    public CodeTestRunDeserializer() {
        super(CodeTestRunEvent.class);
    }

    @Override
    public CodeTestRunEvent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);

        CodeTestRunEvent ctre = new CodeTestRunEvent();

        DeserializationUtils du = new DeserializationUtils();

        fillObjectMap(ctre.getId(), "id", node);
        fillStringMap(ctre.getSource(), "source", node);
        fillStringMap(ctre.getTags(), "tags", node);

        ctre.setResult(du.getCodeTestRunResult(node));

        if (node.get("time") != null) {
            ctre.setTime(javax.xml.bind.DatatypeConverter.parseDateTime(node.get("time").asText()).getTime());
        } else {
            ctre.setTime(new Date());
        }

        return ctre;
    }

}
