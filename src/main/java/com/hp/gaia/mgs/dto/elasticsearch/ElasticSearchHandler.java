package com.hp.gaia.mgs.dto.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.gaia.mgs.dto.BaseEvent;
import com.hp.gaia.mgs.dto.change.ChangeEvent;
import com.hp.gaia.mgs.dto.change.InnerField;
import com.hp.gaia.mgs.dto.change.IssueChangeEvent;
import com.hp.gaia.mgs.dto.change.TestChangeEvent;
import com.hp.gaia.mgs.dto.commit.CodeCommitEvent;
import com.hp.gaia.mgs.dto.generalevent.GeneralEvent;
import com.hp.gaia.mgs.dto.testrun.TestRunEvent;

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
        List<EsPair> esEvents = new ArrayList<EsPair>();
        for (T event : events) {

            if (event != null) {
                Map propsMap = new HashMap<String, String>();

                propsMap.put("timestamp", event.getTime());
                propsMap.put("source", event.getSource());
                propsMap.put("tags", event.getTags());
                propsMap.put("id", event.getId());

                addSpecificTypeData(event, propsMap);

                byte[] esAction = getAction(tenantId, event.getType());
                byte[] esEvent = mapper.writeValueAsBytes(propsMap);
                esEvents.add(new EsPair(esAction, esEvent));
            }

        }

        byte[] allTogether = combineAll(esEvents);
        return allTogether;

    }

    private <T extends BaseEvent> void addSpecificTypeData(T event, Map propsMap) {
        if (event instanceof TestRunEvent) {
            TestRunEvent testRunEvent = (TestRunEvent) event;
            propsMap.put("result", testRunEvent.getResult());
        }

        if (event instanceof CodeCommitEvent) {
            CodeCommitEvent codeCommitEvent = (CodeCommitEvent) event;
            propsMap.put("changed_file", codeCommitEvent.getChangedFilesList());
        }

        if (event instanceof GeneralEvent) {
            GeneralEvent generalEvent = (GeneralEvent) event;
            propsMap.put("data", generalEvent.getData());
        }

        if (event instanceof ChangeEvent)
        {
            ChangeEvent changeEvent = (ChangeEvent) event;
            List<Map<String, Object>> fieldsData = new ArrayList<>();
            for (InnerField field: changeEvent.getFields()) {
                fieldsData.add(field.getMembersAsMap());
            }
            propsMap.put("field",fieldsData);

            if (changeEvent instanceof IssueChangeEvent)
            {
                IssueChangeEvent issueChangeEvent = (IssueChangeEvent) changeEvent;
                propsMap.put("comment", issueChangeEvent.getComments());
            }

            if (changeEvent instanceof TestChangeEvent)
            {
                TestChangeEvent testChangeEvent = (TestChangeEvent) changeEvent;
                propsMap.put("step",testChangeEvent.getSteps());
            }
        }
    }

    //Prepare the action json
    private byte[] getAction(String tenantId, String eventType) throws JsonProcessingException {
        Map actionMap = new HashMap<String, String>();
        Map actionPropsMap = new HashMap<String, String>();
        actionPropsMap.put("_index", "gaia_" + tenantId);
        actionPropsMap.put("_type", eventType);
        actionMap.put("index", actionPropsMap);
        ObjectMapper actionMapper = new ObjectMapper();
        return actionMapper.writeValueAsBytes(actionMap);
    }

    //Combine all of the byte arrays to big one with '/n' separator
    private byte[] combineAll(List<EsPair> eventsInESFormat) {
        byte[] lineSeparator = "\n".getBytes();

        int size=0;
        for(EsPair pair: eventsInESFormat) {
            size += pair.getAction().length +
                    pair.getEvent().length +
                    (lineSeparator.length * 2);
        }

        byte[] allTogether = new byte[size];
        ByteBuffer bf = ByteBuffer.wrap(allTogether);
        for(EsPair singleEvent: eventsInESFormat) {
            bf.put(singleEvent.getAction());
            bf.put(lineSeparator);
            bf.put(singleEvent.getEvent());
            bf.put(lineSeparator);
        }
        return allTogether;
    }

    class EsPair
    {
        private byte[] action;
        private byte[] event;

        EsPair(byte[] action, byte[] event) {
            this.action = action;
            this.event = event;
        }

        byte[] getAction() {
            return action;
        }

        byte[] getEvent() {
            return event;
        }

    }

    /* public static void main(String[] args) {
        String jsonEvents = "[{\"event\":\"code_testrun\",\"id\":{\"method\":\"test[418: combination of <[Passed, Pending, Passed, Passed]>]\",\"build_number\":\"29\",\"class\":\"ActivityStatusCommutativeTest\",\"package\":\"com.hp.alm.platform.dataflow\",\"root_build_number\":\"29\"},\"tags\":{\"scm_branch\":null,\"build_result\":\"UNSTABLE\",\"flow_type\":null,\"build_uri_path\":\"job/ALM-Newton-Compile-Server/29/\",\"schema_version\":null},\"result\":{\"status\":\"PASSED\",\"skipped\":false,\"run_time\":0,\"failed_since\":0,\"age\":0,\"error\":null,\"skipped_message\":null},\"source\":{\"root_job_name\":\"ALM-Newton-Compile-Server\",\"build_server_uri\":\"http://mydtbld0048.isr.hp.com:8888/jenkins\",\"job_name\":\"ALM-Newton-Compile-Server\",\"source_type\":\"jenkins\",\"build_server_host\":\"mydtbld0048.isr.hp.com\"},\"time\":\"2015-08-20T10:37:41\"}]";
        try {
            List<BaseEvent> receivedEvents = new ObjectMapper().readValue(jsonEvents, new TypeReference<List<BaseEvent>>() {
            });
            ElasticSearchHandler converter = new ElasticSearchHandler();
            System.out.println("after conversion:" + new String(converter.convert(receivedEvents, "1234"), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */
}
