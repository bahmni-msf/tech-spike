package com.bahmni.batch.bahmnianalytics.helper;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static Map<String, String> openMRSToPostgresDataTypeMap = new HashMap<String, String>(){{
        put("Datetime","date");
        put("Boolean","text");
        put("Numeric","numeric");
        put("Time","time");
        put("Date","date");
        put("Text","text");
        put("N/A","integer");
        put("Coded","text");
    }};
}
