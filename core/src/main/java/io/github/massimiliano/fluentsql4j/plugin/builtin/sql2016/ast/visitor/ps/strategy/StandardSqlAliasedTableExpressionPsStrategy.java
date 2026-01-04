package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.set.AliasedTableExpression;
import io.github.massimiliano.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.AliasedTableExpressionPsStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard SQL implementation for rendering {@link AliasedTableExpression}.
 * Wraps subqueries in parentheses and appends the alias.
 *
 * @since 1.0
 */
public class StandardSqlAliasedTableExpressionPsStrategy implements AliasedTableExpressionPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            AliasedTableExpression item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        PreparedStatementSpec exprDto = item.expression().accept(visitor, ctx);
        allParameters.addAll(exprDto.parameters());

        // Check if expression is a subquery (SelectStatement)
        if (item.expression() instanceof SelectStatement) {
            sql.append("(").append(exprDto.sql()).append(")");
        } else {
            sql.append(exprDto.sql());
        }

        PreparedStatementSpec aliasDto = item.alias().accept(visitor, ctx);
        allParameters.addAll(aliasDto.parameters());
        if (!aliasDto.sql().isEmpty()) {
            sql.append(" ").append(aliasDto.sql());
        }

        return new PreparedStatementSpec(sql.toString(), allParameters);
    }
}
