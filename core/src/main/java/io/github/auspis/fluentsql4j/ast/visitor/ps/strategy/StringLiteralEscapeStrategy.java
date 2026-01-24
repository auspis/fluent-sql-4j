package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

/**
 * Strategy for escaping string literals in SQL statements.
 * This is used to prevent SQL injection by properly escaping special characters in string values.
 */
public interface StringLiteralEscapeStrategy {
    /**
     * Escapes a string value for safe inclusion in SQL.
     *
     * @param value the string value to escape
     * @return the escaped string value
     */
    String escape(String value);
}
