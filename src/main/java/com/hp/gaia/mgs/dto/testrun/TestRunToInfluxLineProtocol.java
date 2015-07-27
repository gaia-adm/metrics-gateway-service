package com.hp.gaia.mgs.dto.testrun;

import com.hp.gaia.mgs.dto.InfluxLineProtocolConverter;

import java.util.Map;

/**
 * Created by belozovs on 7/22/2015.
 * Convert CodeTestRunEvent object to input data for InfluxDB 0.9
 */
public class TestRunToInfluxLineProtocol implements InfluxLineProtocolConverter<TestRunEvent> {

    /**
     * Convert CodeTestRunEvent to string compatible with InfluxDB 0.9 line protocol
     * For each field separate line create with all its attributes as values and status name as a tag (beside other tags and source)
     * Example of the output (single row):
     * code_testrun,repository=git://github.com/hp/mqm-server,branch=master,browser=firefox,build_label=1.7.0,build_job=backend_job,status=error id_package="com.hp.mqm",id_method="TestLogicalOperators",id_class="FilterBuilder",runTime=130,errorString="NullPointerException: ...",setup_time=35,tear_down_time=20 1447196400000000000
     * @param event CodeTestRunEvent to be converted before inserting to InfluxDB
     * @return row to be inserted to InfluxDB
     *
     */
    @Override
    public String convert(TestRunEvent event) {

        StringBuilder mainSb = new StringBuilder();
        StringBuilder commonPart = new StringBuilder();
        TestRunResult testResult = event.getResult();

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
//            commonPart.append("id_").append(getEscapedString(key)).append("=").append(getQuotedValue(event.getId().get(key))).append(",");
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

        //add steps if presented
        if(testResult.getSteps().isEmpty()){
            //remove last comma
            cutTrailingCharacter(commonPart,',');
            mainSb.append(commonPart).append(" ").append(generateUniqueTimestamp(event.getTime().getTime()));
            //prepare to the next row insert
            mainSb.append(System.lineSeparator());
        } else {
            for (Map<String, Object> stepMap : testResult.getSteps()) {
                StringBuilder steps = new StringBuilder();
                for (String key : stepMap.keySet()) {
                    //collect all attributes of the step
                    if (stepMap.get(key).getClass().equals(java.lang.String.class)) {
                        steps.append("step_").append(getEscapedString(key)).append("=").append(getQuotedValue((String) stepMap.get(key))).append(",");
                    } else {
                        steps.append("step_").append(getEscapedString(key)).append("=").append(stepMap.get(key)).append(",");
                    }
                }
                //remove last comma
                cutTrailingCharacter(steps,',');

                //add timestamp
                mainSb.append(commonPart).append(steps).append(" ").append(generateUniqueTimestamp(event.getTime().getTime()));

                //prepare to the next row insert
                mainSb.append(System.lineSeparator());
            }
        }
        return mainSb.toString();
    }

    private StringBuilder cutTrailingCharacter(StringBuilder sb, char ch){
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ch) {
            sb.setLength(sb.length() - 1);
        }
        return sb;
    }

}
