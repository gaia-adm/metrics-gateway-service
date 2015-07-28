package com.hp.gaia.mgs.dto.change;

import com.hp.gaia.mgs.dto.BaseEvent;

import java.util.List;

/**
 * Created by belozovs on 7/27/2015.
 * Base class for all change events (e.g., IssueChange, TestChange)
 */
public class ChangeEvent extends BaseEvent{

    List<InnerField> fields;
    public List<InnerField> getFields() {
        return fields;
    }

    public void setFields(List<InnerField> fields) {
        this.fields = fields;
    }

    public void addField(InnerField field) {
        this.fields.add(field);
    }
}
