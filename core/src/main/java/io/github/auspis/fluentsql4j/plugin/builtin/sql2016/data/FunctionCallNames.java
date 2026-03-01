package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.data;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class FunctionCallNames {

    // Conditional functions
    public static final String COALESCE = "COALESCE";
    public static final String NULLIF = "NULLIF";

    // String functions
    public static final String CONCAT = "CONCAT";
    public static final String CONCAT_WS = "CONCAT_WS";
    public static final String UPPER = "UPPER";
    public static final String LOWER = "LOWER";
    public static final String TRIM = "TRIM";
    public static final String SUBSTRING = "SUBSTRING";
    public static final String REPLACE = "REPLACE";
    public static final String LENGTH = "LENGTH";
    public static final String CHAR_LENGTH = "CHAR_LENGTH";
    public static final String CHARACTER_LENGTH = "CHARACTER_LENGTH";
    public static final String LEFT = "LEFT";

    // Numeric functions
    public static final String ABS = "ABS";
    public static final String CEIL = "CEIL";
    public static final String FLOOR = "FLOOR";
    public static final String SQRT = "SQRT";
    public static final String ROUND = "ROUND";
    public static final String POWER = "POWER";
    public static final String MOD = "MOD";

    // Date/Time functions
    public static final String CURRENT_DATE = "CURRENT_DATE";
    public static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
    public static final String EXTRACT = "EXTRACT";

    // Type conversion functions
    public static final String CAST = "CAST";

    // Aggregate functions
    public static final String COUNT = "COUNT";
    public static final String SUM = "SUM";
    public static final String AVG = "AVG";
    public static final String MAX = "MAX";
    public static final String MIN = "MIN";

    // Window functions
    public static final String ROW_NUMBER = "ROW_NUMBER";
    public static final String RANK = "RANK";
    public static final String DENSE_RANK = "DENSE_RANK";
    public static final String NTILE = "NTILE";
    public static final String LAG = "LAG";
    public static final String LEAD = "LEAD";

    // JSON functions (SQL:2016)
    public static final String JSON_EXISTS = "JSON_EXISTS";
    public static final String JSON_VALUE = "JSON_VALUE";
    public static final String JSON_QUERY = "JSON_QUERY";

    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    public static class Options {
        public static final String ORDER_BY = "ORDER_BY";
        public static final String SEPARATOR = "SEPARATOR";
        public static final String DISTINCT = "DISTINCT";
    }
}
