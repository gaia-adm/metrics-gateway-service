package com.hp.gaia.mgs.dto;

import java.util.Map;

/**
 * Created by belozovs on 7/22/2015.
 * Interface for converting event to InfluxDB 0.9 line protocol format
 */
public interface InfluxLineProtocolConverter<T extends BaseEvent> {

    String convert(T event);

    /**
     * Following characters must be escaped for InfluxDB: spaces, commas and double-quotes
     *
     * @param str String to replace spaces, commas and double-quotes
     * @return String after the replacement
     */
    default String getEscapedString(String str) {
        return str.replace("\"", "\\\"").replace(" ", "\\ ").replace(",", "\\,");
    }

    /**
     * String values must be double-quoted when inserting to InfluxDB
     *
     * @param str String value to be double-quoated
     * @return double-quoted value
     */
    default String getQuotedValue(String str) {
        return "\"" + str + "\"";
    }


    /**
     * Generate unique timestamp with nanosecond precision in order to prevent attempt of insert to InfluxDB with non-unique timestamp
     * It is possible that several events come with the same timestamp and the same tags.
     * The original timestamp precision is milliseconds, we "increase" the precision in order to promise its uniqueness
     * microseconds part is populated the last part of IP
     * nanoseconds part is populated with running integer number in the range between 0 to 1000.
     *
     * @param time original timestamp
     * @return "more precise" timestamp
     */
    default Long generateUniqueTimestamp(long time) {

        //add leading zeroes (to numeric) to make it 3-digit
        String microseconds = String.format("%03d", TimestampRandomizer.getInstance().getPartOfIp());
        String nanoseconds = String.format("%03d", TimestampRandomizer.getInstance().nextNumber());

        return Long.valueOf(String.valueOf(time).concat(microseconds).concat(nanoseconds));
    }

    default void cutTrailingComma(StringBuilder sb) {
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
            sb.setLength(sb.length() - 1);
        }
    }

    default String createTimestampLattermost(BaseEvent event) {

        StringBuilder sb = new StringBuilder();
        //add timestamp that is built to prevent duplicate timestamps
        sb.append(" ").append(generateUniqueTimestamp(event.getTime().getTime())); //switch to nanoseconds, as InfluxDB requires

        //prepare to the next row insert
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    default String createTags(BaseEvent event) {
        StringBuilder sb = new StringBuilder();
        for (String key : event.getSource().keySet()) {
            if (event.getSource().get(key) != null) {
                sb.append(",").append(getEscapedString(key)).append("=").append(getEscapedString(event.getSource().get(key)));
            }
        }
        for (String key : event.getTags().keySet()) {
            if (event.getSource().get(key) != null) {
                sb.append(",").append(getEscapedString(key)).append("=").append(getEscapedString(event.getTags().get(key)));
            }
        }

        return sb.toString();
    }

    default String createDataFromMap(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            if (map.get(key) != null && map.get(key).getClass().equals(java.lang.String.class)) {
                sb.append(getEscapedString(key)).append("=").append(getQuotedValue((String) map.get(key))).append(",");
            } else {
                sb.append(getEscapedString(key)).append("=").append(map.get(key)).append(",");
            }
        }
        return sb.toString();
    }
}
