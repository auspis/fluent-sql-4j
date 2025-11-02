package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class StandardSqlNullScalarExpressionRenderStrategy implements ExpressionRenderStrategy {

    public String render(Object expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return "";
    }
}
