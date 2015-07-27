package com.hp.gaia.mgs.dto.change;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by belozovs on 7/16/2015.
 * Field of IssueChangeEvent
 */
public class InnerField {

    @NotNull
    String name;
    @NotNull
    String to;
    String from;
    Long ttc;
    Map<String, String> customFields = new HashMap<>();

    public InnerField(String name) {
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

    public Long getTtc() {
        return ttc;
    }

    public void setTtc(Long ttc) {
        this.ttc = ttc;
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public void addCustomField(String name, String value) {
        customFields.put(name, value);
    }

    public Map<String, Object> getMembersAsMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("to", to);
        map.put("from", from);
        map.put("ttc", ttc);
        map.putAll(customFields);
        return map;
    }
}
