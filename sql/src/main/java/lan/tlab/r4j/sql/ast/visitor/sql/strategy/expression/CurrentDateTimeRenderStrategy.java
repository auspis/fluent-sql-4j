package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.CurrentDateTime;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface CurrentDateTimeRenderStrategy extends ExpressionRenderStrategy {

    public String render(CurrentDateTime functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
