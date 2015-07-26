package com.hp.gaia.mgs.dto.change;


import com.hp.gaia.mgs.dto.InfluxLineProtocolConverter;

/**
 * Created by belozovs on 7/22/2015.
 * Convert IssueChangeEvent object to input data for InfluxDB 0.9
 */
public class IssueChangeToInfluxLineProtocol implements InfluxLineProtocolConverter<IssueChangeEvent> {


    /**
     * Convert IssueChangeEvent to string compatible with InfluxDB 0.9 line protocol
     * For each field separate line created with all its attributes as values and field name as a tag (beside other tags and source)
     * Example of the output (single row):
     * issue_change,server=http://alm-saas.hp.com,domain=IT,project=Project\ A,workspace=CRM,user=bob,field=Status id_uid="1111",to="Open",from="New",ttc=124 1437595557534000000
     *
     * @param event event to be converted
     * @return row to be inserted to InfluxDB
     */
    @Override
    public String convert(IssueChangeEvent event) {

        StringBuilder mainSb = new StringBuilder();
        for (IssueField field : event.getFields()) {
            //create measurement and tags (tags, source and field name)
            mainSb.append(getEscapedString(event.getType()));
            for (String key : event.getSource().keySet()) {
                mainSb.append(",").append(getEscapedString(key)).append("=").append(getEscapedString(event.getSource().get(key)));
            }
            for (String key : event.getTags().keySet()) {
                mainSb.append(",").append(getEscapedString(key)).append("=").append(getEscapedString(event.getTags().get(key)));
            }
            mainSb.append(",").append("field").append("=").append(getEscapedString(field.getName()));

            //separator between measurement+tags and data part of the string
            mainSb.append(" ");
            //create data part
            //id and values (fields, result, etc.)
            for (String key : event.getId().keySet()) {
                //note: id value is always String and should be quoted
                mainSb.append("id_").append(getEscapedString(key)).append("=").append(getQuotedValue(event.getId().get(key))).append(",");
            }

            mainSb.append("to=").append(getQuotedValue(field.getTo())).append(",");
            if (field.getFrom() != null) {
                mainSb.append("from=").append(getQuotedValue(field.getFrom())).append(",");
            }
            if (field.getTtc() != null) {
                mainSb.append("ttc=").append(field.getTtc()).append(",");
            }
            if (field.getCustomFields() != null) {
                for (String key : field.getCustomFields().keySet()) {
                    mainSb.append(getEscapedString(key)).append("=").append(getQuotedValue(field.getCustomFields().get(key))).append(",");
                }
            }
            if (mainSb.length() > 0 && mainSb.charAt(mainSb.length() - 1) == ',') {
                mainSb.setLength(mainSb.length() - 1);
            }
            //add timestamp that is built to prevent duplicate timestamps
            mainSb.append(" ").append(generateUniqueTimestamp(event.getTime().getTime())); //switch to nanoseconds, as InfluxDB requires

            //prepare to the next row insert
            mainSb.append(System.lineSeparator());
        }


        return mainSb.toString();

    }


}
