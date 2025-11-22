package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface CurrentDateRenderStrategy extends ExpressionRenderStrategy {

    public String render(CurrentDate functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
