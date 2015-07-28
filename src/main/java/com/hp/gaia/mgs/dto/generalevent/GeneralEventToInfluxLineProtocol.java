package com.hp.gaia.mgs.dto.generalevent;


import com.hp.gaia.mgs.dto.InfluxLineProtocolConverter;
import com.hp.gaia.mgs.dto.commit.CodeCommitEvent;

import java.util.Map;

/**
 * Created by belozovs on 7/22/2015.
 * Convert GeneralEvent object to input data for InfluxDB 0.9
 */
public class GeneralEventToInfluxLineProtocol implements InfluxLineProtocolConverter<GeneralEvent> {


    /**
     * Convert GeneralEvent to string compatible with InfluxDB 0.9 line protocol
     *
     * @param event event to be converted
     * @return row to be inserted to InfluxDB
     */
    @Override
    public String convert(GeneralEvent event) {

        StringBuilder mainSb = new StringBuilder();

            //create measurement and tags (tags, source)
            mainSb.append(getEscapedString(event.getType()));
            mainSb.append(createTags(event));
            mainSb.append(",").append("dimension=general");
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
