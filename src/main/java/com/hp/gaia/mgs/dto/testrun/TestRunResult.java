package com.hp.gaia.mgs.dto.testrun;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by belozovs on 7/16/2015.
 */
class TestRunResult {

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

    public void addCustomField(String name, Object value){
        customFields.put(name, value);
    }
}
