package com.hp.gaia.mgs.dto.testrun;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by belozovs on 7/16/2015.
 */
@JsonDeserialize(using = TestRunDeserializer.class)
public class AlmTestRunEvent extends TestRunEvent {

    public final static String EVENT_TYPE = "tm_testrun";

    AlmTestRunResult result;

    public AlmTestRunEvent() {
        this.setType(EVENT_TYPE);
    }

    @Override
    public AlmTestRunResult getResult() {
        return result;
    }

    @Override
    public <T extends TestRunResult> void setResult(T result) {
        this.result = (AlmTestRunResult) result;
    }

    @Override
    public Map<String, Object> getValues() {

        Map<String, Object> valuesMap = new HashMap<>();

        valuesMap.put("status", result.getStatus());
        valuesMap.put("runTime", result.getRunTime());
        if (result.getErrorString() != null) {
            valuesMap.put("errorString", result.getErrorString());
        }
        if (!result.getCustomFields().isEmpty()) {
            for (String customField : result.getCustomFields().keySet()) {
                valuesMap.put(customField, result.getCustomFields().get(customField));
            }
        }
        List<Map<String, Object>> steps =  result.getSteps();
        for(Map<String, Object> step : steps){
            String prefix = (String) step.get("name");
            for(String key : step.keySet()){
                if(!key.equals("name")){
                    valuesMap.put(prefix+"_"+key, step.get(key));
                }
            }
        }

        return valuesMap;
    }
}
