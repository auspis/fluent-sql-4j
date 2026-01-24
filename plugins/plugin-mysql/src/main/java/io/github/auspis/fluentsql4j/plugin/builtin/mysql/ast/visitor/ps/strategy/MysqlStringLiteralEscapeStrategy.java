package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.StringLiteralEscapeStrategy;

/**
 * MySQL string literal escape strategy.
 * Escapes single quotes by doubling them following MySQL standard.
 */
public class MysqlStringLiteralEscapeStrategy implements StringLiteralEscapeStrategy {

    @Override
    public String escape(String value) {
        return value.replace("'", "''");
    }
}
