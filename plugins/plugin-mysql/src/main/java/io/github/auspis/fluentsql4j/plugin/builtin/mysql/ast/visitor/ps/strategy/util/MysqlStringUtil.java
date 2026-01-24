package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.util;

public final class MysqlStringUtil {

    private MysqlStringUtil() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Escapes single quotes in string values for MySQL by doubling them.
     * This prevents SQL injection when embedding string values in SQL.
     *
     * @param value the string value to escape
     * @return the escaped string value
     */
    public static String escape(String value) {
        return value.replace("'", "''");
    }
}
