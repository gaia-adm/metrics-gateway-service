package com.hp.gaia.mgs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by belozovs on 7/15/2015.
 */
public class BaseEvent {

    @JsonProperty("event")
    String type;
    Date time;
    Map<String, String> id;
    Map<String, String> source;
    Map<String, String> tags;

    public BaseEvent() {

        source = new HashMap<>();
        tags = new HashMap<>();
        id = new HashMap<>();

    }

    public String getType() {
        return type;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getId() {
        return id;
    }

    public void setId(Map<String, String> id) {
        this.id = id;
    }

    public Map<String, String> getSource() {
        return source;
    }

    public void setSource(Map<String, String> source) {
        this.source = source;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
