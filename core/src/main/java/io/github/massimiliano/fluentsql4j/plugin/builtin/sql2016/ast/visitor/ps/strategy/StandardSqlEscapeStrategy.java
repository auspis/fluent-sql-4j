package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.EscapeStrategy;

public class StandardSqlEscapeStrategy implements EscapeStrategy {

    @Override
    public String apply(String value) {
        return String.format("\"%s\"", value);
    }
}
