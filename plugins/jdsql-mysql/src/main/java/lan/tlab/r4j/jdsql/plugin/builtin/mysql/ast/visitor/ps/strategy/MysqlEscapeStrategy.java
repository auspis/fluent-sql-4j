package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.EscapeStrategy;

public class MysqlEscapeStrategy implements EscapeStrategy {

    @Override
    public String apply(String value) {
        return String.format("`%s`", value);
    }
}
