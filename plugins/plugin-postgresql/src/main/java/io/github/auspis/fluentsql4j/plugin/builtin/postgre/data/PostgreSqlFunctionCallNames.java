package io.github.auspis.fluentsql4j.plugin.builtin.postgre.data;

import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.data.FunctionCallNames;
import lombok.NoArgsConstructor;

public class PostgreSqlFunctionCallNames extends FunctionCallNames {

    public static final String STRING_AGG = "STRING_AGG";
    public static final String ARRAY_AGG = "ARRAY_AGG";
    public static final String JSONB_AGG = "JSONB_AGG";
    public static final String DATE_TRUNC = "DATE_TRUNC";
    public static final String AGE = "AGE";
    public static final String TO_CHAR = "TO_CHAR";

    @NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Options extends FunctionCallNames.Options {}
}
