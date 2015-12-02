package com.hp.gaia.mgs.dto.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.BaseEvent;
import com.hp.gaia.mgs.dto.testrun.CodeTestRunEvent;
import com.hp.gaia.mgs.rest.context.TenantContextHolder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by tsadok on 20/11/2015.
 */
public class ElasticSearchHandler {

    /*
    * We convert the events into ES bulk format.
    * Bulk format composed of: action/n data/n action/n data/
    * More info here: https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html
    * */
    public <T extends BaseEvent> byte[] convert(Collection<T> events, String tenantId) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        //Go over the events and convert them to ES format
        List<byte[]> eventsInESFormat = new ArrayList<byte[]>();
        for (T event : events) {

            Map propsMap = new HashMap<String, String>();

            // for now we handle only code_testrun events
            if ((event != null)
                   && event.getType().equals(CodeTestRunEvent.EVENT_TYPE)
                   && (event instanceof CodeTestRunEvent)) {

                CodeTestRunEvent codeTestRunEvent = (CodeTestRunEvent) event;

                codeTestRunEvent.getSource().keySet().
                        forEach(key -> propsMap.put(key, codeTestRunEvent.getSource().get(key)));

                codeTestRunEvent.getTags().keySet().
                        forEach(key -> propsMap.put(key, codeTestRunEvent.getTags().get(key)));

                codeTestRunEvent.getId().keySet().
                        forEach(key -> propsMap.put(key, codeTestRunEvent.getId().get(key)));

                codeTestRunEvent.getResult().getMembersAsMap().entrySet().
                        forEach(key -> propsMap.put(key, codeTestRunEvent.getResult().getMembersAsMap().get(key)));

                propsMap.put("timestamp", codeTestRunEvent.getTime());

                eventsInESFormat.add(mapper.writeValueAsBytes(propsMap));
            }
        }

        byte[] actionInESFormat = getAction(tenantId);
        byte[] allTogether = combineAll(actionInESFormat, eventsInESFormat);
        return allTogether;

    }

    //Prepare the action json
    private byte[] getAction(String tenantId) throws JsonProcessingException {
        Map actionMap = new HashMap<String, String>();
        Map actionPropsMap = new HashMap<String, String>();
        actionPropsMap.put("_index", "gaia_" + tenantId);
        actionPropsMap.put("_type", CodeTestRunEvent.EVENT_TYPE);
        actionMap.put("index", actionPropsMap);
        ObjectMapper actionMapper = new ObjectMapper();
        return actionMapper.writeValueAsBytes(actionMap);
    }

    //Combine all of the byte arrays to big one with '/n' separator
    private byte[] combineAll(byte[] actionInESFormat, List<byte[]> eventsInESFormat) {
        byte[] lineSeparator = "\n".getBytes();

        int size=0;
        for(byte[] array: eventsInESFormat)
            size+=array.length;

        size+=((lineSeparator.length*2)+actionInESFormat.length)*eventsInESFormat.size();

        byte[] allTogether = new byte[size];
        ByteBuffer bf = ByteBuffer.wrap(allTogether);
        for(byte[] singleEvent: eventsInESFormat) {
            bf.put(actionInESFormat);
            bf.put(lineSeparator);
            bf.put(singleEvent);
            bf.put(lineSeparator);
        }
        return allTogether;
    }

    /*public static void main(String[] args) {
        String jsonEvents = "[{\"event\":\"code_testrun\",\"id\":{\"method\":\"test[418: combination of <[Passed, Pending, Passed, Passed]>]\",\"build_number\":\"29\",\"class\":\"ActivityStatusCommutativeTest\",\"package\":\"com.hp.alm.platform.dataflow\",\"root_build_number\":\"29\"},\"tags\":{\"scm_branch\":null,\"build_result\":\"UNSTABLE\",\"flow_type\":null,\"build_uri_path\":\"job/ALM-Newton-Compile-Server/29/\",\"schema_version\":null},\"result\":{\"status\":\"PASSED\",\"skipped\":false,\"run_time\":0,\"failed_since\":0,\"age\":0,\"error\":null,\"skipped_message\":null},\"source\":{\"root_job_name\":\"ALM-Newton-Compile-Server\",\"build_server_uri\":\"http://mydtbld0048.isr.hp.com:8888/jenkins\",\"job_name\":\"ALM-Newton-Compile-Server\",\"source_type\":\"jenkins\",\"build_server_host\":\"mydtbld0048.isr.hp.com\"},\"time\":\"2015-08-20T10:37:41\"}]";
        try {
            List<BaseEvent> receivedEvents = new ObjectMapper().readValue(jsonEvents, new TypeReference<List<BaseEvent>>() {
            });
            ElasticSearchHandler converter = new ElasticSearchHandler();
            System.out.println("after conversion:" + new String(converter.convert(receivedEvents), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
