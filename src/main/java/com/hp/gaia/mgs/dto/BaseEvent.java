package com.hp.gaia.mgs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hp.gaia.mgs.dto.change.IssueChangeEvent;
import com.hp.gaia.mgs.dto.change.TestChangeEvent;
import com.hp.gaia.mgs.dto.commit.CodeCommitEvent;
import com.hp.gaia.mgs.dto.testrun.AlmTestRunEvent;
import com.hp.gaia.mgs.dto.testrun.CodeTestRunEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by belozovs on 7/15/2015.
 *
 * Base class for all kinds of events
 *
 */


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "event")
@JsonSubTypes({
        @JsonSubTypes.Type(value = IssueChangeEvent.class, name = "issue_change"),
        @JsonSubTypes.Type(value = TestChangeEvent.class, name = "test_change"),
        @JsonSubTypes.Type(value = CodeCommitEvent.class, name = "code_commit"),
        @JsonSubTypes.Type(value = AlmTestRunEvent.class, name = "tm_testrun"),
        @JsonSubTypes.Type(value = CodeTestRunEvent.class, name = "code_testrun")})
public class BaseEvent {

    //EVENT_TYPE is not used here directly but needed for using with generic code
    @SuppressWarnings("unused")
    public static String EVENT_TYPE;

    @JsonProperty("event")
    String type;
    Date time;
    Map<String, Object> id;
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

    public Map<String, Object> getId() {
        return id;
    }

    public void setId(Map<String, Object> id) {
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
