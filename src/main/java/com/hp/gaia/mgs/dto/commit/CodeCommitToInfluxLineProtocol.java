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
        //ugly workaround needed to save multiple rows (row per file) for the same commit as tags are the same and only values are different
        //i variable used to move each file timestamp in the same commit
        int i=0;
        //Nothing sent to DB if no files exist in the files change list
        for (Map<String, Object> fileChange : event.getChangedFilesList()) {
            //create measurement and tags (tags, source and field name)
            mainSb.append(getEscapedString(event.getType()));
            for (String key : event.getSource().keySet()) {
                mainSb.append(",").append(getEscapedString(key)).append("=").append(getEscapedString(event.getSource().get(key)));
            }
            for (String key : event.getTags().keySet()) {
                mainSb.append(",").append(getEscapedString(key)).append("=").append(getEscapedString(event.getTags().get(key)));
            }

            //separator between measurement+tags and data part of the string
            mainSb.append(" ");
            //create data part
            //id and values (fields, result, etc.)
            for (String key : event.getId().keySet()) {
                //note: id value is always String and should be quoted
                mainSb.append("id_").append(getEscapedString(key)).append("=").append(getQuotedValue(event.getId().get(key))).append(",");
            }
            mainSb.append("totalChangedFiles=").append(event.getChangedFilesList().size()).append(",");

            for (String key : fileChange.keySet()) {
                if (fileChange.get(key).getClass().equals(java.lang.String.class)) {
                    mainSb.append(getEscapedString(key)).append("=").append(getQuotedValue((String) fileChange.get(key))).append(",");
                } else {
                    mainSb.append(getEscapedString(key)).append("=").append(fileChange.get(key)).append(",");
                }
            }

            if (mainSb.length() > 0 && mainSb.charAt(mainSb.length() - 1) == ',') {
                mainSb.setLength(mainSb.length() - 1);
            }
            //add timestamp
            //TBD - boris: make InfluxDBManager in event-indexer adding &precision=ms query param to "writeToDB" URL and remove 1000000 from here
            mainSb.append(" ").append((event.getTime().getTime() + (i++)) * 1000000); //switch to nanoseconds, as InfluxDB requires

            //prepare to the next row insert
            mainSb.append(System.lineSeparator());
        }


        return mainSb.toString();

    }


}
