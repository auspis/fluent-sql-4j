package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.EscapeStrategy;

public class MysqlEscapeStrategy implements EscapeStrategy {

    @Override
    public String apply(String value) {
        return String.format("`%s`", value);
    }
}
