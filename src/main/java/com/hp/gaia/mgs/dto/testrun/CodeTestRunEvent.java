package com.hp.gaia.mgs.dto.testrun;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by belozovs on 7/16/2015.
 */
@JsonDeserialize(using = TestRunDeserializer.class)
public class CodeTestRunEvent extends TestRunEvent {

    public final static String EVENT_TYPE = "code_testrun";

    TestRunResult result;

    public TestRunResult getResult() {
        return result;
    }

    @Override
    public void setResult(TestRunResult result) {
        this.result = result;
    }

    @Override
    public Map<String, Object> getValues() {

        Map<String, Object> valuesMap = new HashMap<>();

        valuesMap.put("status", result.getStatus());
        valuesMap.put("runTime", result.getRunTime());
        if(result.getErrorString() != null){
            valuesMap.put("errorString", result.getErrorString());
        }
        if(!result.getCustomFields().isEmpty()){
            for(String customField : result.getCustomFields().keySet()) {
                valuesMap.put(customField, result.getCustomFields().get(customField));
            }
        }

        return valuesMap;
    }
}
