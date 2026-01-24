package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlStringLiteralEscapeStrategy;

/**
 * MySQL string literal escape strategy.
 * MySQL follows the same escaping rules as Standard SQL - single quotes are escaped by doubling them.
 */
public class MysqlStringLiteralEscapeStrategy extends StandardSqlStringLiteralEscapeStrategy {
    // MySQL uses the same escaping strategy as Standard SQL
}
