package com.hp.gaia.mgs.dto.testrun;

import java.util.*;

/**
 * Created by belozovs on 7/16/2015.
 * Base class for all kinds of TestRunResults: either manual (ALM) or automatic test
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

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getSteps(){
        return new ArrayList();
    }
}
