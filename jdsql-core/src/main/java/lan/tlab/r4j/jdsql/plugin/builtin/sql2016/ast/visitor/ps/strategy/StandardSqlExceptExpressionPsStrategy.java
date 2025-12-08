package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.set.ExceptExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ExceptExpressionPsStrategy;

public class StandardSqlExceptExpressionPsStrategy implements ExceptExpressionPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            ExceptExpression expression, PreparedStatementRenderer renderer, AstContext ctx) {
        PreparedStatementSpec leftDto = expression.left().accept(renderer, ctx);
        PreparedStatementSpec rightDto = expression.right().accept(renderer, ctx);

        String sql = String.format(
                "((%s) %s (%s))", leftDto.sql(), expression.distinct() ? "EXCEPT" : "EXCEPT ALL", rightDto.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(leftDto.parameters());
        parameters.addAll(rightDto.parameters());

        return new PreparedStatementSpec(sql, parameters);
    }
}
