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
import com.hp.gaia.mgs.dto.testrun.AlmTestRunEvent;
import com.hp.gaia.mgs.dto.testrun.CodeTestRunEvent;
import com.hp.gaia.mgs.dto.testrun.TestRunEvent;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by tsadok on 20/11/2015.
 */
public class ElasticSearchHandler {

    /*
    * We convert the event into structured json
    * */
    public <T extends BaseEvent> byte[] convert(T event) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        Map propsMap = new HashMap<String, String>();

        propsMap.put("timestamp", event.getTime());
        propsMap.put("source", event.getSource());
        propsMap.put("tags", event.getTags());
        propsMap.put("id", event.getId());

        addSpecificTypeData(event, propsMap);

        return mapper.writeValueAsBytes(propsMap);
    }

    private <T extends BaseEvent> void addSpecificTypeData(T event, Map propsMap) {
        if (event instanceof TestRunEvent) {
            TestRunEvent testRunEvent = (TestRunEvent) event;
            propsMap.put("result", testRunEvent.getResult().getMembersAsMap());

            if (testRunEvent instanceof AlmTestRunEvent) {
                AlmTestRunEvent almTestRunEvent = (AlmTestRunEvent) testRunEvent;
                propsMap.put("testrun_step", almTestRunEvent.getResult().getSteps());
            }
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
                propsMap.put("test_step",testChangeEvent.getSteps());
            }
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
