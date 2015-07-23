package com.hp.gaia.mgs.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by belozovs on 5/27/2015.
 */


public class OldMetric {

    String metric;
    String category;
    String name;
    String source;
    Long timestamp;
    List<String> tags = new ArrayList<>();
    List<OldMeasurement> measurements = new ArrayList<>();
    List<OldEvent> events = new ArrayList<>();

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<OldMeasurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<OldMeasurement> measurements) {
        this.measurements = measurements;
    }

    public List<OldEvent> getEvents() {
        return events;
    }

    public void setEvents(List<OldEvent> events) {
        this.events = events;
    }
}
