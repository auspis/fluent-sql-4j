package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.set;

import lan.tlab.r4j.jdsql.ast.common.expression.set.AliasedTableExpression;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.set.AliasedTableExpressionRenderStrategy;

public class StandardSqlAliasedTableExpressionRenderStrategy implements AliasedTableExpressionRenderStrategy {
    @Override
    public String render(AliasedTableExpression aliased, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder();

        // Check if expression is a subquery (SelectStatement)
        if (aliased.expression() instanceof SelectStatement) {
            sql.append("(")
                    .append(aliased.expression().accept(sqlRenderer, ctx))
                    .append(")");
        } else {
            sql.append(aliased.expression().accept(sqlRenderer, ctx));
        }

        String alias = aliased.alias().accept(sqlRenderer, ctx);
        if (!alias.isEmpty()) {
            sql.append(" ").append(alias);
        }

        return sql.toString();
    }
}
