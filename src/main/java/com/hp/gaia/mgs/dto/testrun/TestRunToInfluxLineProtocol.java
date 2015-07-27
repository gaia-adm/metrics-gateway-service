package com.hp.gaia.mgs.dto.testrun;

import com.hp.gaia.mgs.dto.InfluxLineProtocolConverter;

import java.util.Map;

/**
 * Created by belozovs on 7/22/2015.
 * Convert CodeTestRunEvent object to input data for InfluxDB 0.9
 */
public class TestRunToInfluxLineProtocol implements InfluxLineProtocolConverter<TestRunEvent> {


    @Override
    public String convert(TestRunEvent event) {

        StringBuilder mainSb = new StringBuilder();

        //create measurement and tags (tags, source)
        mainSb.append(getEscapedString(event.getType()));
        mainSb.append(createTags(event));
        //add field name as a tag
        mainSb.append(",").append("dimension=result");
        //separator between measurement+tags and data part of the string
        mainSb.append(" ");
        //create data part
        mainSb.append(createDataFromMap(event.getId()));
        mainSb.append(createDataFromMap(event.getResult().getMembersAsMap()));

        cutTrailingComma(mainSb);
        mainSb.append(createTimestampLattermost(event));


        for (Map<String, Object> stepMap : event.getResult().getSteps()) {
            //create measurement and tags (tags, source)
            mainSb.append(getEscapedString(event.getType()));
            mainSb.append(createTags(event));
            //add field name as a tag
            mainSb.append(",").append("dimension=test-step-result");
            //separator between measurement+tags and data part of the string
            mainSb.append(" ");
            //create data part
            mainSb.append(createDataFromMap(event.getId()));
            mainSb.append(createDataFromMap(stepMap));

            cutTrailingComma(mainSb);
            mainSb.append(createTimestampLattermost(event));
        }


        return mainSb.toString();
    }

}