package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface ColumnReferenceRenderStrategy extends ExpressionRenderStrategy {

    String render(ColumnReference expression, SqlRenderer sqlRenderer, AstContext ctx);
}
