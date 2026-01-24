package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.StringLiteralEscapeStrategy;

/**
 * Standard SQL string literal escape strategy.
 * Escapes single quotes by doubling them as per SQL standard.
 */
public class StandardSqlStringLiteralEscapeStrategy implements StringLiteralEscapeStrategy {

    @Override
    public String escape(String value) {
        return value.replace("'", "''");
    }
}
