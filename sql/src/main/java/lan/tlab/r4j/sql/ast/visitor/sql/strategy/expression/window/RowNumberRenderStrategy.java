package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.sql.ast.expression.scalar.call.window.RowNumber;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public interface RowNumberRenderStrategy extends ExpressionRenderStrategy {

    String render(RowNumber rowNumber, SqlRenderer sqlRenderer, AstContext ctx);
}
