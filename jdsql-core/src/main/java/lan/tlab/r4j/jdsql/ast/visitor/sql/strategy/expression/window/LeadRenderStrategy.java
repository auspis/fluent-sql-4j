package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Lead;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public interface LeadRenderStrategy extends ExpressionRenderStrategy {

    String render(Lead lead, SqlRenderer sqlRenderer, AstContext ctx);
}
