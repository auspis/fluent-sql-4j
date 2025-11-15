package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.set.IntersectExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.IntersectRenderStrategy;

public class StandardSqlIntersectRenderStrategy implements IntersectRenderStrategy {

    @Override
    public String render(IntersectExpression expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "((%s) %s (%s))",
                expression.leftSetExpression().accept(sqlRenderer, ctx),
                expression.type().equals(IntersectExpression.IntersectType.INTERSECT_ALL)
                        ? "INTERSECT ALL"
                        : "INTERSECT",
                expression.rightSetExpression().accept(sqlRenderer, ctx));
    }
}
