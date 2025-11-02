package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class StandardSqlComparisonRenderStrategy implements ExpressionRenderStrategy {

    public String render(Comparison expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s %s %s",
                expression.lhs().accept(sqlRenderer, ctx),
                expression.operator().getSqlSymbol(),
                expression.rhs().accept(sqlRenderer, ctx));
    }
}
