package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.bool.Comparison;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class ComparisonRenderStrategy implements ExpressionRenderStrategy {

    public String render(Comparison expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s %s %s",
                expression.getLhs().accept(sqlRenderer, ctx),
                expression.getOperator().getSqlSymbol(),
                expression.getRhs().accept(sqlRenderer, ctx));
    }
}
