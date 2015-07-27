package com.hp.gaia.mgs.dto.commit;


import com.hp.gaia.mgs.dto.InfluxLineProtocolConverter;

import java.util.Map;

/**
 * Created by belozovs on 7/22/2015.
 * Convert CodeCommitEvent object to input data for InfluxDB 0.9
 */
public class CodeCommitToInfluxLineProtocol implements InfluxLineProtocolConverter<CodeCommitEvent> {


    /**
     * Convert CodeCommitEvent to string compatible with InfluxDB 0.9 line protocol
     * For each file separate line created with all its attributes as values
     * Artificial commitId is set as a value
     * Artificial field representing a number of files changed in the commit is introduced for further statistics
     * Example of the output (single row):
     *
     * @param event event to be converted
     * @return row to be inserted to InfluxDB
     */
    @Override
    public String convert(CodeCommitEvent event) {

        StringBuilder mainSb = new StringBuilder();

        //Nothing sent to DB if no files exist in the files change list
        for (Map<String, Object> fileChange : event.getChangedFilesList()) {
            //create measurement and tags (tags, source and field name)
            //create measurement and tags (tags, source)
            mainSb.append(getEscapedString(event.getType()));
            mainSb.append(createTags(event));

            //separator between measurement+tags and data part of the string
            mainSb.append(" ");

            //create data part
            mainSb.append(createDataFromMap(event.getId()));
            mainSb.append(createDataFromMap(fileChange));

            cutTrailingComma(mainSb);
            mainSb.append(createTimestampLattermost(event));
        }

        return mainSb.toString();
    }


}
