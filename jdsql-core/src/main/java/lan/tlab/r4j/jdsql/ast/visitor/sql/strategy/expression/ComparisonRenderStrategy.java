package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface ComparisonRenderStrategy extends ExpressionRenderStrategy {

    String render(Comparison expression, SqlRenderer sqlRenderer, AstContext ctx);
}
