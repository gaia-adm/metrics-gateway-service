package com.hp.gaia.mgs.dto.testrun;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hp.gaia.mgs.dto.BaseEvent;

/**
 * Created by belozovs on 7/16/2015.
 */
public abstract class TestRunEvent extends BaseEvent{

    public abstract <T extends TestRunResult> void setResult(T result);

}
