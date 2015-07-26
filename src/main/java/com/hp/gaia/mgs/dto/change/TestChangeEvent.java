package com.hp.gaia.mgs.dto.change;

import com.hp.gaia.mgs.dto.BaseEvent;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by belozovs on 7/24/2015.
 */
public class TestChangeEvent extends BaseEvent {

    public final static String EVENT_TYPE = "test_change";

    List<IssueField> fields;
    //TODO - boris: steps and attachments - like steps in TestRunEvent? Everything is OK but no group by

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
