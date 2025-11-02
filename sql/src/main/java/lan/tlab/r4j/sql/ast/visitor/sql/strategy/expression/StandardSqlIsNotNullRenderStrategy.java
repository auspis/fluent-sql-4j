package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.predicate.IsNotNull;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class StandardSqlIsNotNullRenderStrategy implements ExpressionRenderStrategy {

    public String render(IsNotNull expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("%s IS NOT NULL", expression.expression().accept(sqlRenderer, ctx));
    }
}
