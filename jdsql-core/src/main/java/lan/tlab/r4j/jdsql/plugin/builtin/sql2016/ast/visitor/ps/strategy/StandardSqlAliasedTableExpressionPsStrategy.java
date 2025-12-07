package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.set.AliasedTableExpression;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.AliasedTableExpressionPsStrategy;

/**
 * Standard SQL implementation for rendering {@link AliasedTableExpression}.
 * Wraps subqueries in parentheses and appends the alias.
 *
 * @since 1.0
 */
public class StandardSqlAliasedTableExpressionPsStrategy implements AliasedTableExpressionPsStrategy {

    @Override
    public PsDto handle(AliasedTableExpression item, PreparedStatementRenderer visitor, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        PsDto exprDto = item.expression().accept(visitor, ctx);
        allParameters.addAll(exprDto.parameters());

        // Check if expression is a subquery (SelectStatement)
        if (item.expression() instanceof SelectStatement) {
            sql.append("(").append(exprDto.sql()).append(")");
        } else {
            sql.append(exprDto.sql());
        }

        PsDto aliasDto = item.alias().accept(visitor, ctx);
        allParameters.addAll(aliasDto.parameters());
        if (!aliasDto.sql().isEmpty()) {
            sql.append(" ").append(aliasDto.sql());
        }

        return new PsDto(sql.toString(), allParameters);
    }
}
