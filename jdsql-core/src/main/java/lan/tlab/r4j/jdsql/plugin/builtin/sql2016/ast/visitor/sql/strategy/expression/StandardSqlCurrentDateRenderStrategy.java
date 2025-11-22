package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.CurrentDateRenderStrategy;

public class StandardSqlCurrentDateRenderStrategy implements CurrentDateRenderStrategy {

    @Override
    public String render(CurrentDate functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return "CURRENT_DATE()";
    }
}
