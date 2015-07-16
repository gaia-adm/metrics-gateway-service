package com.hp.gaia.mgs.dto.issuechange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hp.gaia.mgs.dto.BaseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by belozovs on 7/15/2015.
 *
 * No support for comments 
 *
 */

@JsonDeserialize(using = IssueChangeDeserializer.class)
public class IssueChangeEvent extends BaseEvent {

    public final static String EVENT_TYPE = "issue_change";

    @JsonProperty("fields")
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

class IssueField {
    String name;
    String to;
    String from;
    String ttc;
    Map<String, String> customFields = new HashMap<>();

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

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }

    public void addCustomField(String name, String value) {
        customFields.put(name, value);
    }

}
