package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.NullScalarExpressionRenderStrategy;

public class StandardSqlNullScalarExpressionRenderStrategy implements NullScalarExpressionRenderStrategy {

    @Override
    public String render(Object expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return "";
    }
}
