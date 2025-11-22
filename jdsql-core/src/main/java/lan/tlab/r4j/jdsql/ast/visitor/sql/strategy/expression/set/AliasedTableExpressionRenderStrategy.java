package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.set;

import lan.tlab.r4j.jdsql.ast.common.expression.set.AliasedTableExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public interface AliasedTableExpressionRenderStrategy extends ExpressionRenderStrategy {
    String render(AliasedTableExpression aliased, SqlRenderer sqlRenderer, AstContext ctx);
}
