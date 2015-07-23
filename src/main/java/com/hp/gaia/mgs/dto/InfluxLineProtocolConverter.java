package com.hp.gaia.mgs.dto;

/**
 * Created by belozovs on 7/22/2015.
 * Interface for converting event to InfluxDB 0.9 line protocol format
 */
public interface InfluxLineProtocolConverter <T extends BaseEvent>{

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

}
