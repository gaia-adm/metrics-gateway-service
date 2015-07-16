package com.hp.gaia.mgs.dto.issuechange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hp.gaia.mgs.dto.BaseEvent;

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
    public final String foo = this.getClass().getName();

    //@JsonProperty("fields")
    List<IssueField> fields;

    public IssueChangeEvent() {
        this.setType(EVENT_TYPE);
        fields = new ArrayList<>();
    }

    public List<IssueField> getFields() {
        return fields;
    }

    public void setFields(List<IssueField> fields) {
        this.fields = fields;
    }

    public void addField(IssueField field) {
        this.fields.add(field);
    }
}

