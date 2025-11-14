package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.CurrentDateTime;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CurrentDateTimeRenderStrategy;

public class StandardSqlCurrentDateTimeRenderStrategy implements CurrentDateTimeRenderStrategy {

    @Override
    public String render(CurrentDateTime functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return "CURRENT_TIMESTAMP()";
    }
}
