package com.hp.gaia.mgs.dto.testrun;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by belozovs on 7/16/2015.
 */
@JsonDeserialize(using = TestRunDeserializer.class)
public class AlmTestRunEvent extends TestRunEvent {

    public final static String EVENT_TYPE = "tm_testrun";

    AlmTestRunResult result;

    public AlmTestRunResult getResult() {
        return result;
    }

    @Override
    public <T extends TestRunResult> void setResult(T result) {
        this.result = (AlmTestRunResult) result;
    }
}
