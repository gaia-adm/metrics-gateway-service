package com.hp.gaia.mgs.dto.testrun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by belozovs on 7/16/2015.
 */
public class AlmTestRunResult extends TestRunResult {

    List<Map<String, Object>> steps = new ArrayList<>();

    @Override
    public List<Map<String, Object>> getSteps() {
        return steps;
    }

    public void setSteps(List<Map<String, Object>> steps) {
        this.steps = steps;
    }

    public void addStep(Map<String, Object> step) {
        steps.add(step);
    }


}
