package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface DateArithmeticRenderStrategy extends ExpressionRenderStrategy {

    public String render(DateArithmetic functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
