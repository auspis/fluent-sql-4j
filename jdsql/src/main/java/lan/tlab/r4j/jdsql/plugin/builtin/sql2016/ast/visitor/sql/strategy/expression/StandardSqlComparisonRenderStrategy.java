package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ComparisonRenderStrategy;

public class StandardSqlComparisonRenderStrategy implements ComparisonRenderStrategy {

    @Override
    public String render(Comparison expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s %s %s",
                expression.lhs().accept(sqlRenderer, ctx),
                expression.operator().getSqlSymbol(),
                expression.rhs().accept(sqlRenderer, ctx));
    }
}
