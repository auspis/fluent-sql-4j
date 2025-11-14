package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.escape;

import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;

public class MysqlEscapeStrategy implements EscapeStrategy {

    @Override
    public String apply(String value) {
        return String.format("`%s`", value);
    }
}
