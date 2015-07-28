package com.hp.gaia.mgs.dto.generalevent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hp.gaia.mgs.dto.BaseEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by belozovs on 7/24/2015.
 * General event for testing purpose mostly
 *
 */
@JsonDeserialize(using = GeneralEventDeserializer.class)
public class GeneralEvent extends BaseEvent {

    public final static String EVENT_TYPE = "general";

    Map<String, Object> data;

    public GeneralEvent() {
        this.setType(EVENT_TYPE);
        this.data = new HashMap<>();
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
