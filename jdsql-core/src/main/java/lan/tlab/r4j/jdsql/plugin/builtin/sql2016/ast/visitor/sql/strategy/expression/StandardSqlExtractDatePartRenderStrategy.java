package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.ExtractDatePart;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ExtractDatePartRenderStrategy;

public class StandardSqlExtractDatePartRenderStrategy implements ExtractDatePartRenderStrategy {

    @Override
    public String render(ExtractDatePart functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s(%s)",
                functionCall.functionName(), functionCall.dateExpression().accept(sqlRenderer, ctx));
    }
}
