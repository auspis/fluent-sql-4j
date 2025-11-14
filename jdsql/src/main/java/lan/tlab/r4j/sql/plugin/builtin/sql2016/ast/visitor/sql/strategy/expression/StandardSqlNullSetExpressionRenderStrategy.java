package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.set.NullSetExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.NullSetExpressionRenderStrategy;

public class StandardSqlNullSetExpressionRenderStrategy implements NullSetExpressionRenderStrategy {

    @Override
    public String render(NullSetExpression expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return "";
    }
}
