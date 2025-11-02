package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface LiteralRenderStrategy extends ExpressionRenderStrategy {

    String render(Literal<?> literal, SqlRenderer sqlRenderer, AstContext ctx);
}
