package com.hp.gaia.mgs.dto.change;


import com.hp.gaia.mgs.dto.InfluxLineProtocolConverter;

import java.util.Map;

/**
 * Created by belozovs on 7/22/2015.
 * Convert any Change event object (e.g., IssueChange, TestChange) to input data for InfluxDB 0.9
 */
public class ChangeToInfluxLineProtocol implements InfluxLineProtocolConverter<ChangeEvent> {

    /**
     * Convert ChangeEvent to string compatible with InfluxDB 0.9 line protocol
     * Conversion of single input event may result to multiple records in DB
     * Examples:
     * Input (issue_change):
     * {"event":"issue_change","time":"2015-07-27T23:00:00Z","source":{"server":"http://alm-saas.hp.com","domain":"IT","project":"Project A"},"id":{"uid":"1122"},"tags":{"workspace":"CRM","user":"bob"},"fields":[{"name":"Status","from":"New","to":"Open","ttc":124},{"name":"Priority","to":"2-Medium"}],"comments":[{"topic":"re: Problem to delpoy on AWS","text":"larin fdsfsdf, fsdfds fsdfsfs","time_since_last_post(h)":12.5}]}
     * Output (issue_change x 3):
     * issue_change,server=http://alm-saas.hp.com,domain=IT,project=Project\ A,dimension=field uid="1122",ttc=124,name="Status",from="New",to="Open" 1438038000000000000
     * issue_change,server=http://alm-saas.hp.com,domain=IT,project=Project\ A,dimension=field uid="1122",name="Priority",to="2-Medium" 1438038000000000001
     * issue_change,server=http://alm-saas.hp.com,domain=IT,project=Project\ A,dimension=comment uid="1122",topic="re: Problem to delpoy on AWS",text="larin fdsfsdf, fsdfds fsdfsfs",time_since_last_post(h)=12.5 1438038000000000002
     * Input (test_change):
     * {"time":"2015-07-27T23:00:00Z","id":{"uid":"2341"},"event":"test_change","source":{"server":"http://alm-saas.hp.com","domain":"IT","project":"Project A"},"tags":{"workspace":"CRM","user":"bob"},"fields":[{"name":"State","from":"Maintenance","to":"Ready","ttc(d)":11}],"steps":[{"new":10,"modified":3,"deleted":1}],"attachments":[{"name":"readme.docx","size":"1.3M"}]}
     * Output (test_change x 3):
     * test_change,server=http://alm-saas.hp.com,domain=IT,project=Project\ A,dimension=field uid="2341",name="State",ttc(d)="11",from="Maintenance",to="Ready" 1438038000000000003
     * test_change,server=http://alm-saas.hp.com,domain=IT,project=Project\ A,dimension=attachment uid="2341",size="1.3M",name="readme.docx" 1438038000000000004
     * test_change,server=http://alm-saas.hp.com,domain=IT,project=Project\ A,dimension=step uid="2341",new=10,deleted=1,modified=3 1438038000000000005
     *
     * @param event event to be converted
     * @return row to be inserted to InfluxDB
     */
    @Override
    public String convert(ChangeEvent event) {

        StringBuilder mainSb = new StringBuilder();
        //for fields
        for (InnerField field : event.getFields()) {
            //create measurement and tags (tags, source)
            mainSb.append(getEscapedString(event.getType()));
            mainSb.append(createTags(event));
            //add field name as a tag
            mainSb.append(",").append(DB_FIELD_PREFIX).append("dimension=field");

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
                mainSb.append(",").append(DB_FIELD_PREFIX).append("dimension=comment");
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
                mainSb.append(",").append(DB_FIELD_PREFIX).append("dimension=attachment");
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
                mainSb.append(",").append(DB_FIELD_PREFIX).append("dimension=step");
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
