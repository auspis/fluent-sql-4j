package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface ScalarSubqueryRenderStrategy extends ExpressionRenderStrategy {

    String render(ScalarSubquery expression, SqlRenderer sqlRenderer, AstContext ctx);
}
