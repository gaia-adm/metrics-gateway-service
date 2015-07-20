package com.hp.gaia.mgs.dto.issuechange;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by belozovs on 7/16/2015.
 */
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

/*    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }*/

    public void addCustomField(String name, String value) {
        customFields.put(name, value);
    }

}
