package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.EscapeStrategy;

public class StandardSqlEscapeStrategy implements EscapeStrategy {

    @Override
    public String apply(String value) {
        return String.format("\"%s\"", value);
    }
}
