package com.hp.gaia.mgs.dto.change;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by belozovs on 7/15/2015.
 *
 * Object representing issue change event
 *
 */

@JsonDeserialize(using = IssueChangeDeserializer.class)
public class IssueChangeEvent extends ChangeEvent {

    public final static String EVENT_TYPE = "issue_change";

    List<Map<String, Object>> comments;

    public IssueChangeEvent() {
        this.setType(EVENT_TYPE);
        fields = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public List<Map<String, Object>> getComments() {
        return comments;
    }

    public void setComments(List<Map<String, Object>> comments) {
        this.comments = comments;
    }

    public void addComment(Map<String, Object> comment) {
        comments.add(comment);
    }

}

