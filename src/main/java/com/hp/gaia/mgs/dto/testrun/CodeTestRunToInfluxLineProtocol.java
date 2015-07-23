/*
package com.hp.gaia.mgs.dto.testrun;

import com.hp.gaia.mgs.dto.InfluxLineProtocolConverter;

*/
/**
 * Created by belozovs on 7/22/2015.
 * Convert CodeTestRunEvent object to input data for InfluxDB 0.9
 *//*

public class CodeTestRunToInfluxLineProtocol implements InfluxLineProtocolConverter<CodeTestRunEvent> {

    */
/**
     * Convert CodeTestRunEvent to string compatible with InfluxDB 0.9 line protocol
     * For each field separate line create with all its attributes as values and status name as a tag (beside other tags and source)
     * Example of the output (single row):
     * code_testrun,repository=git://github.com/hp/mqm-server,branch=master,browser=firefox,build_label=1.7.0,build_job=backend_job,status=error id_package="com.hp.mqm",id_method="TestLogicalOperators",id_class="FilterBuilder",runTime=130,errorString="NullPointerException: ...",setup_time=35,tear_down_time=20 1447196400000000000
     * @param event CodeTestRunEvent to be converted before inserting to InfluxDB
     * @return row to be inserted to InfluxDB
     *
     *//*

    @Override
    public String convert(CodeTestRunEvent event) {

        StringBuilder mainSb = new StringBuilder();

        TestRunResult testResult = event.getResult();

        //create measurement and tags (tags, source and status)
        mainSb.append(getEscapedString(event.getType()));
        for (String key : event.getSource().keySet()) {
            mainSb.append(",").append(getEscapedString(key)).append("=").append(getEscapedString(event.getSource().get(key)));
        }
        for (String key : event.getTags().keySet()) {
            mainSb.append(",").append(getEscapedString(key)).append("=").append(getEscapedString(event.getTags().get(key)));
        }
        mainSb.append(",").append("status").append("=").append(getEscapedString(testResult.getStatus()));

        //separator between measurement+tags and data part of the string
        mainSb.append(" ");
        //create a data part
        //id and values (fields, result, etc.)
        for (String key : event.getId().keySet()) {
            //note: id value is always String and should be quoted (at least thanks to id_ prefix)
            mainSb.append("id_").append(getEscapedString(key)).append("=").append(getQuotedValue(event.getId().get(key))).append(",");
        }

        mainSb.append("runTime=").append(testResult.getRunTime()).append(",");
        mainSb.append("errorString=").append(getQuotedValue(testResult.getErrorString())).append(",");
        if (testResult.getCustomFields() != null) {
            for (String key : testResult.getCustomFields().keySet()) {
                if(testResult.getCustomFields().get(key).getClass().equals(java.lang.String.class)){
                    mainSb.append(getEscapedString(key)).append("=").append(getQuotedValue((String) testResult.getCustomFields().get(key))).append(",");
                } else {
                    mainSb.append(getEscapedString(key)).append("=").append(testResult.getCustomFields().get(key)).append(",");
                }

            }
        }
        if (mainSb.length() > 0 && mainSb.charAt(mainSb.length() - 1) == ',') {
            mainSb.setLength(mainSb.length() - 1);
        }
        //add timestamp
        //TBD - boris: make InfluxDBManager in event-indexer adding &precision=ms query param to "writeToDB" URL and remove 1000000 from here
        mainSb.append(" ").append(event.getTime().getTime() * 1000000); //switch to nanoseconds, as InfluxDB requires

        //prepare to the next row insert
        mainSb.append(System.lineSeparator());

        return mainSb.toString();
    }
}
*/
