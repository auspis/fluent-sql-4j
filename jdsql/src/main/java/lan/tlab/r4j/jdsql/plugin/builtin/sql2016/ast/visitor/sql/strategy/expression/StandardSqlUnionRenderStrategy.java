package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.set.UnionExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.set.UnionExpression.UnionType;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.UnionRenderStrategy;

public class StandardSqlUnionRenderStrategy implements UnionRenderStrategy {

    @Override
    public String render(UnionExpression expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "((%s) %s (%s))",
                expression.left().accept(sqlRenderer, ctx),
                (expression.type() == UnionType.UNION_DISTINCT ? "UNION" : "UNION ALL"),
                expression.right().accept(sqlRenderer, ctx));
    }
}
