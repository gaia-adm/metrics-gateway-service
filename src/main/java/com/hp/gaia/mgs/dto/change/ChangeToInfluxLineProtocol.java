package com.hp.gaia.mgs.dto.change;


import com.hp.gaia.mgs.dto.InfluxLineProtocolConverter;

import java.util.Map;

/**
 * Created by belozovs on 7/22/2015.
 * Convert IssueChangeEvent object to input data for InfluxDB 0.9
 */
public class ChangeToInfluxLineProtocol implements InfluxLineProtocolConverter<ChangeEvent> {


    @Override
    public String convert(ChangeEvent event) {

        StringBuilder mainSb = new StringBuilder();
        //for fields
        for (InnerField field : event.getFields()) {
            //create measurement and tags (tags, source)
            mainSb.append(getEscapedString(event.getType()));
            mainSb.append(createTags(event));
            //add field name as a tag
            mainSb.append(",").append("datatype=field");

            //separator between measurement+tags and data part of the string
            mainSb.append(" ");
            //create data part
            mainSb.append(createDataFromMap(event.getId()));
            mainSb.append(createDataFromMap(field.getMembersAsMap()));

            cutTrailingComma(mainSb);
            mainSb.append(createTimestampLattermost(event));
        }

        //for comments
        if (event.getClass().equals(IssueChangeEvent.class)) {
            for (Map<String, Object> commentMap : ((IssueChangeEvent)event).getComments()) {
                //create measurement and tags (tags, source)
                mainSb.append(getEscapedString(event.getType()));
                mainSb.append(createTags(event));
                //add "comment" as a tag
                mainSb.append(",").append("datatype=comment");
                //separator between measurement+tags and data part of the string
                mainSb.append(" ");
                //create data part
                mainSb.append(createDataFromMap(event.getId()));
                mainSb.append(createDataFromMap(commentMap));

                cutTrailingComma(mainSb);
                mainSb.append(createTimestampLattermost(event));
            }
        }


        if(event.getClass().equals(TestChangeEvent.class)) {
            //for attachments
            for (Map<String, Object> attachmentsMap : ((TestChangeEvent)event).getAttachments()) {
                //create measurement and tags (tags, source)
                mainSb.append(getEscapedString(event.getType()));
                mainSb.append(createTags(event));
                //add "attachment" as a tag
                mainSb.append(",").append("datatype=attachment");
                //separator between measurement+tags and data part of the string
                mainSb.append(" ");
                //create data part
                mainSb.append(createDataFromMap(event.getId()));
                mainSb.append(createDataFromMap(attachmentsMap));

                cutTrailingComma(mainSb);
                mainSb.append(createTimestampLattermost(event));
            }

            //for steps
            for (Map<String, Object> stepsMap : ((TestChangeEvent)event).getSteps()) {
                //create measurement and tags (tags, source)
                mainSb.append(getEscapedString(event.getType()));
                mainSb.append(createTags(event));
                //add "step" as a tag
                mainSb.append(",").append("datatype=step");
                //separator between measurement+tags and data part of the string
                mainSb.append(" ");
                //create data part
                mainSb.append(createDataFromMap(event.getId()));
                mainSb.append(createDataFromMap(stepsMap));

                cutTrailingComma(mainSb);
                mainSb.append(createTimestampLattermost(event));
            }
        }

        return mainSb.toString();
    }






}
