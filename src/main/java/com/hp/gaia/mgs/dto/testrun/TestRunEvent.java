package com.hp.gaia.mgs.dto.testrun;

import com.hp.gaia.mgs.dto.BaseEvent;

/**
 * Created by belozovs on 7/16/2015.
 * Base class for all types of TestRun (either manual in ALM or automatic)
 */
public abstract class TestRunEvent extends BaseEvent {

    public abstract <T extends TestRunResult> void setResult(T result);

    public abstract <T extends TestRunResult> T getResult();

}
