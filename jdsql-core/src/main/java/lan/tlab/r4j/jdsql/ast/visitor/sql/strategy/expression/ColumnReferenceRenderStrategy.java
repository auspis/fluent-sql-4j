package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface ColumnReferenceRenderStrategy extends ExpressionRenderStrategy {

    String render(ColumnReference expression, SqlRenderer sqlRenderer, AstContext ctx);
}
