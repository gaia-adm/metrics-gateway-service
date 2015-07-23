/*
package com.hp.gaia.mgs.dto.testrun;

import com.hp.gaia.mgs.dto.InfluxLineProtocolConverter;

import java.util.Map;

*/
/**
 * Created by belozovs on 7/22/2015.
 * Convert AlmTestRunEvent object to input data for InfluxDB 0.9
 *//*

public class AlmTestRunToInfluxLineProtocol implements InfluxLineProtocolConverter<AlmTestRunEvent> {

    */
/**
     * Convert AlmTestRunEvent to string compatible with InfluxDB 0.9 line protocol
     * For each field separate line create with all its attributes as values and status name as a tag (beside other tags and source)
     * Example of the output (single row):
     * tm_testrun,server=http://alm-saas.hp.com,domain=IT,project=Project\ A,workspace=CRM,testset=Regression,type=Manual,user=john,status=Passed id_instance="245",id_test_id="34",runTime=380,errorString="null",step0_name="step 1",step0_status="Passed",step0_runt_time=12,step1_name="step 2",step1_status="Skipped",step2_name="step 3",step2_status="Passed",step2_runt_time=368 1447196400000000000
     * @param event AlmTestRunEvent to be converted before inserting to InfluxDB
     * @return row to be inserted to InfluxDB
     *
     *//*

    @Override
    public String convert(AlmTestRunEvent event) {
        StringBuilder mainSb = new StringBuilder();

        AlmTestRunResult testResult = event.getResult();

        StringBuilder commonPart = new StringBuilder();
        //create a common part before steps separation
        commonPart.append(getEscapedString(event.getType()));
        for (String key : event.getSource().keySet()) {
            commonPart.append(",").append(getEscapedString(key)).append("=").append(getEscapedString(event.getSource().get(key)));
        }
        for (String key : event.getTags().keySet()) {
            commonPart.append(",").append(getEscapedString(key)).append("=").append(getEscapedString(event.getTags().get(key)));
        }
        commonPart.append(",").append("status").append("=").append(getEscapedString(testResult.getStatus()));

        //separator between measurement+tags and data part of the string
        commonPart.append(" ");
        //create a data part
        //id and values (fields, result, etc.)
        for (String key : event.getId().keySet()) {
            //note: id value is always String
            commonPart.append("id_").append(getEscapedString(key)).append("=").append(getQuotedValue(event.getId().get(key))).append(",");
        }

        commonPart.append("runTime=").append(testResult.getRunTime()).append(",");
        if(testResult.getErrorString()!= null) {
            commonPart.append("errorString=").append(getQuotedValue(testResult.getErrorString())).append(",");
        }
        if (testResult.getCustomFields() != null) {
            for (String key : testResult.getCustomFields().keySet()) {
                if(testResult.getCustomFields().get(key).getClass().equals(java.lang.String.class)){
                    commonPart.append(getEscapedString(key)).append("=").append(getQuotedValue((String) testResult.getCustomFields().get(key))).append(",");
                } else {
                    commonPart.append(getEscapedString(key)).append("=").append(testResult.getCustomFields().get(key)).append(",");
                }

            }
        }

        for(Map<String, Object> stepMap : testResult.getSteps()){
            StringBuilder steps = new StringBuilder();
            for(String key : stepMap.keySet()) {
                //collect all attributes of the step
                if (stepMap.get(key).getClass().equals(java.lang.String.class)) {
                    steps.append(getEscapedString(key)).append("=").append(getQuotedValue((String) stepMap.get(key))).append(",");
                } else {
                    steps.append(getEscapedString(key)).append("=").append(stepMap.get(key)).append(",");
                }
            }
            //remove last comma
            if (steps.length() > 0 && steps.charAt(steps.length() - 1) == ',') {
                steps.setLength(steps.length() - 1);
            }
            //add timestamp
            //TBD - boris: make InfluxDBManager in event-indexer adding &precision=ms query param to "writeToDB" URL and remove 1000000 from here
            mainSb.append(commonPart).append(steps).append(" ").append(event.getTime().getTime() * 1000000); //switch to nanoseconds, as InfluxDB requires

            //prepare to the next row insert
            mainSb.append(System.lineSeparator());
        }


*/
/*        //create measurement and tags (tags, source and status)
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
            //note: id value is always String
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
        //steps are added as values also
        int i=0;
        for(Map<String, Object> stepMap : testResult.getSteps()){
            for(String key : stepMap.keySet()){
                if(stepMap.get(key).getClass().equals(java.lang.String.class)){
                    mainSb.append("step").append(i).append("_").append(getEscapedString(key)).append("=").append(getQuotedValue((String) stepMap.get(key))).append(",");
                } else {
                    mainSb.append("step").append(i).append("_").append(getEscapedString(key)).append("=").append(stepMap.get(key)).append(",");
                }
            }
            i++;
        }

        if (mainSb.length() > 0 && mainSb.charAt(mainSb.length() - 1) == ',') {
            mainSb.setLength(mainSb.length() - 1);
        }
        //add timestamp
        //TBD - boris: make InfluxDBManager in event-indexer adding &precision=ms query param to "writeToDB" URL and remove 1000000 from here
        mainSb.append(" ").append(event.getTime().getTime() * 1000000); //switch to nanoseconds, as InfluxDB requires

        //prepare to the next row insert
        mainSb.append(System.lineSeparator());*//*


        return mainSb.toString();
    }

}
*/
