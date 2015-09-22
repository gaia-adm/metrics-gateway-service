package com.hp.gaia.mgs.dto.testrun;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Created by belozovs on 7/16/2015.
 * Base class for all kinds of TestRunResults: either manual (ALM) or automatic test
 */
class TestRunResult {

    @NotNull
    private String status;
    private Long runTime;
    private String errorString;
    private Map<String, Object> customFields = new HashMap<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRunTime() {
        return runTime;
    }

    public void setRunTime(Long runTime) {
        this.runTime = runTime;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    public Map<String, Object> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getSteps(){
        return new ArrayList();
    }

    public Map<String, Object> getMembersAsMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        if(runTime!=null){
            map.put("runTime", runTime);
        }
        if(errorString!=null) {
            map.put("errorString", errorString);
        }
        map.putAll(customFields);
        return map;
    }
}
