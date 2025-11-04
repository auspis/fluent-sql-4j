package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.set;

import lan.tlab.r4j.sql.ast.expression.set.AliasedTableExpression;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.set.AliasedTableExpressionRenderStrategy;

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
