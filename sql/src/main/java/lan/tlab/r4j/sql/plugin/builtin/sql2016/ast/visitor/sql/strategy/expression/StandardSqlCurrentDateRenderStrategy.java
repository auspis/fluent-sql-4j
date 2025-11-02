package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CurrentDateRenderStrategy;

public class StandardSqlCurrentDateRenderStrategy implements CurrentDateRenderStrategy {

    @Override
    public String render(CurrentDate functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return "CURRENT_DATE()";
    }
}
