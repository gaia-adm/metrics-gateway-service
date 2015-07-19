package com.hp.gaia.mgs.dto.issuechange;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hp.gaia.mgs.dto.AbstractBaseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by belozovs on 7/15/2015.
 * <p>
 * No support for comments
 */

@JsonDeserialize(using = IssueChangeDeserializer.class)
public class IssueChangeEvent extends AbstractBaseEvent {

    public final static String EVENT_TYPE = "issue_change";

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

    @Override
    public Map<String, Object> getValues() {

        Map<String, Object> valuesMap = new HashMap<>();

        for (IssueField issueField : fields) {
            String prefix = issueField.getName();
            if (issueField.getFrom() != null) {
                valuesMap.put(prefix + "_from", issueField.getFrom());
            }
            if (issueField.getTo() != null) {
                valuesMap.put(prefix + "_to", issueField.getTo());
            }
            if (issueField.getTtc() != null) {
                valuesMap.put(prefix + "_ttc", issueField.getTtc());
            }
            if (!issueField.getCustomFields().isEmpty()) {
                for (String customField : issueField.getCustomFields().keySet()) {
                    valuesMap.put(prefix + "_" + customField, issueField.getCustomFields().get(customField));
                }
            }
        }


        return valuesMap;
    }
}

