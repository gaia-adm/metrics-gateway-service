package com.hp.gaia.mgs.dto.commit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.hp.gaia.mgs.dto.CommonDeserializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by belozovs on 7/15/2015.
 * CodeCommit event deserializer
 */
public class CodeCommitDeserializer extends JsonDeserializer<CodeCommitEvent> implements CommonDeserializationUtils {

    Logger logger = LoggerFactory.getLogger(CodeCommitDeserializer.class);

    @Override
    public CodeCommitEvent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);

        CodeCommitEvent cce = new CodeCommitEvent();

        Iterator<JsonNode> fileNodes = node.get("files").elements();

        while (fileNodes.hasNext()) {
            JsonNode fileNode = fileNodes.next();
            cce.addChangedFile(fetchFileChange(fileNode));
        }

        fillObjectMap(cce.getId(), "id", node);
        fillStringMap(cce.getSource(), "source", node);
        fillStringMap(cce.getTags(), "tags", node);


        if (node.get("time") != null) {
            cce.setTime(javax.xml.bind.DatatypeConverter.parseDateTime(node.get("time").asText()).getTime());
        } else {
            cce.setTime(new Date());
        }

        return cce;
    }

    private Map<String, Object> fetchFileChange(JsonNode fileNode) {

        Map<String, Object> fileDataMap = new HashMap<>();

        Iterator<String> fieldNames = fileNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            fileDataMap.put(fieldName, fileNode.get(fieldName));
        }

        return fileDataMap;
    }

}
