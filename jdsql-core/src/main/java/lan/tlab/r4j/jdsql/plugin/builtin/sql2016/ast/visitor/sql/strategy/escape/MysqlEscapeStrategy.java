package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.escape;

import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.escape.EscapeStrategy;

public class MysqlEscapeStrategy implements EscapeStrategy {

    @Override
    public String apply(String value) {
        return String.format("`%s`", value);
    }
}
