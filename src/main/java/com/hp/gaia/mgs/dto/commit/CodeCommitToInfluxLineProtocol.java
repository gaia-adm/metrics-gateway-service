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
     * Conversion of single input event may result to multiple records in DB
     * Example:
     * Input:
     * {"event":"code_commit","time":"2015-11-10T23:00:00Z","source":{"repository":"git://github.com/hp/mqm-server","branch":"master"},"id":{"uid":"8ad3535acb2a724eb0058fa071c788d48ab6978e"},"tags":{"user":"alex"},"files":[{"file":"README.md","loc":10},{"file":" src/main/java/managers/RabbitmqManager.java","loc":-14}]}
     * Output (2 rows):
     * code_commit,repository=git://github.com/hp/mqm-server,branch=master,dimension=file uid="8ad3535acb2a724eb0058fa071c788d48ab6978e",loc=10,file="README.md" 1438038000000000006
     * code_commit,repository=git://github.com/hp/mqm-server,branch=master,dimension=file uid="8ad3535acb2a724eb0058fa071c788d48ab6978e",loc=-14,file=" src/main/java/managers/RabbitmqManager.java" 1438038000000000007
     *
     * @param event event to be converted
     * @return row to be inserted to InfluxDB
     */
    @Override
    public String convert(CodeCommitEvent event) {

        StringBuilder mainSb = new StringBuilder();

        //Nothing sent to DB if no files exist in the files change list
        for (Map<String, Object> fileChange : event.getChangedFilesList()) {
            //create measurement and tags (tags, source)
            mainSb.append(getEscapedString(event.getType()));
            mainSb.append(createTags(event));
            //add "file" as a tag
            mainSb.append(",").append(DB_FIELD_PREFIX).append("dimension=file");
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
