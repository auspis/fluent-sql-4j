package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface ComparisonRenderStrategy extends ExpressionRenderStrategy {

    String render(Comparison expression, SqlRenderer sqlRenderer, AstContext ctx);
}
