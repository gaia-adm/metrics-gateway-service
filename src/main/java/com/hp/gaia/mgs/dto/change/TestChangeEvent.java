package com.hp.gaia.mgs.dto.change;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hp.gaia.mgs.dto.BaseEvent;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by belozovs on 7/24/2015.
 *
 * Object representing test change event
 */
@JsonDeserialize(using = TestChangeDeserializer.class)
public class TestChangeEvent extends ChangeEvent {

    public final static String EVENT_TYPE = "test_change";

    List<Map<String, Object>> attachments;
    List<Map<String, Object>> steps;

    public TestChangeEvent() {
        this.setType(EVENT_TYPE);
        fields = new ArrayList<>();
        attachments = new ArrayList<>();
        steps= new ArrayList<>();
    }

    public List<Map<String, Object>> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Map<String, Object>> attachments) {
        this.attachments = attachments;
    }

    public List<Map<String, Object>> getSteps() {
        return steps;
    }

    public void setSteps(List<Map<String, Object>> steps) {
        this.steps = steps;
    }
}
