package com.hp.gaia.mgs.dto.generalevent;


import com.hp.gaia.mgs.dto.InfluxLineProtocolConverter;

/**
 * Created by belozovs on 7/22/2015.
 * Convert GeneralEvent object to input data for InfluxDB 0.9
 */
public class GeneralEventToInfluxLineProtocol implements InfluxLineProtocolConverter<GeneralEvent> {


    /**
     * Convert GeneralEvent to string compatible with InfluxDB 0.9 line protocol
     * Single input event always produces single output record
     * Example:
     * Input:
     * {"event":"general","time":"2015-07-27T23:00:00Z","source":{"origin":"notyourbusiness"},"id":{"uid":"12345"},"tags":{"tag1":"foo","tag2":"boo"},"data":{"field1":"value1","field2":"value2","field3":3}}
     * Output:
     * general,origin=notyourbusiness,dimension=general uid="12345",field1="value1",field3=3,field2="value2" 1438038000000000013
     * @param event event to be converted
     * @return row to be inserted to InfluxDB
     */
    @Override
    public String convert(GeneralEvent event) {

        StringBuilder mainSb = new StringBuilder();

            //create measurement and tags (tags, source)
            mainSb.append(getEscapedString(event.getType()));
            mainSb.append(createTags(event));
            mainSb.append(",").append(DB_FIELD_PREFIX).append("dimension=general");
            //separator between measurement+tags and data part of the string
            mainSb.append(" ");

            //create data part
            mainSb.append(createDataFromMap(event.getId()));
            mainSb.append(createDataFromMap(event.getData()));

            cutTrailingComma(mainSb);
            mainSb.append(createTimestampLattermost(event));

        return mainSb.toString();
    }


}
