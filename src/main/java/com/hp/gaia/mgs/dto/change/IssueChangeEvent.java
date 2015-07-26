package com.hp.gaia.mgs.dto.change;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hp.gaia.mgs.dto.BaseEvent;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by belozovs on 7/15/2015.
 * <p>
 * No support for comments
 */

@JsonDeserialize(using = IssueChangeDeserializer.class)
public class IssueChangeEvent extends BaseEvent {

    public final static String EVENT_TYPE = "issue_change";

    @NotNull
    List<IssueField> fields;

    //TODO - boris: comments - like steps in TestRunEvent, with no group by

    public IssueChangeEvent() {
        this.setType(EVENT_TYPE);
        fields = new ArrayList<>();
    }

    public List<IssueField> getFields() {
        return fields;
    }

    @SuppressWarnings("unused")
    public void setFields(List<IssueField> fields) {
        this.fields = fields;
    }

    public void addField(IssueField field) {
        this.fields.add(field);
    }

}

