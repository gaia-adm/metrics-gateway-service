package com.hp.gaia.mgs.dto.testrun;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by belozovs on 7/16/2015.
 * Version of TestRunEvent for automatic test
 * Main differentiator: EVENT_TYPE
 */
@JsonDeserialize(using = TestRunDeserializer.class)
public class CodeTestRunEvent extends TestRunEvent {

    public final static String EVENT_TYPE = "code_testrun";

    TestRunResult result;

    public CodeTestRunEvent() {
        this.setType(EVENT_TYPE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TestRunResult getResult() {
        return result;
    }

    @Override
    public void setResult(TestRunResult result) {
        this.result = result;
    }

}
