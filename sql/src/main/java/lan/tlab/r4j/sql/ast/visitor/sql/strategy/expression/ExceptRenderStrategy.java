package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.set.ExceptExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface ExceptRenderStrategy extends ExpressionRenderStrategy {

    String render(ExceptExpression expression, SqlRenderer sqlRenderer, AstContext ctx);
}
