package com.hp.gaia.mgs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by belozovs on 7/15/2015.
 */

@JsonDeserialize(using = IssueChangeDeserializer.class)
public class IssueChangeEvent extends BaseEvent {

    private final static String ICE_TYPE = "issue_change";

    @JsonProperty("fields")
    List<IssueField> fields;

    public IssueChangeEvent() {
        type = ICE_TYPE;
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

class IssueField {
    String name;
    String to;
    String from;
    String ttc;

    public IssueField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTtc() {
        return ttc;
    }

    public void setTtc(String ttc) {
        this.ttc = ttc;
    }

}
