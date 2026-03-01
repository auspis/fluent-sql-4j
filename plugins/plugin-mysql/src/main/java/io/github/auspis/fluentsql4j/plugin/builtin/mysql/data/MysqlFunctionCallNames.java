package io.github.auspis.fluentsql4j.plugin.builtin.mysql.data;

import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.data.FunctionCallNames;
import lombok.NoArgsConstructor;

public class MysqlFunctionCallNames extends FunctionCallNames {

    public static final String GROUP_CONCAT = "GROUP_CONCAT";
    public static final String IF = "IF";
    public static final String IFNULL = "IFNULL";
    public static final String DATE_FORMAT = "DATE_FORMAT";

    @NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class Options extends FunctionCallNames.Options {}
}
