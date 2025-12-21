package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.set.ExceptExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ExceptExpressionPsStrategy;

public class StandardSqlExceptExpressionPsStrategy implements ExceptExpressionPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            ExceptExpression expression, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        PreparedStatementSpec leftDto = expression.left().accept(astToPsSpecVisitor, ctx);
        PreparedStatementSpec rightDto = expression.right().accept(astToPsSpecVisitor, ctx);

        String sql = String.format(
                "((%s) %s (%s))", leftDto.sql(), expression.distinct() ? "EXCEPT" : "EXCEPT ALL", rightDto.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(leftDto.parameters());
        parameters.addAll(rightDto.parameters());

        return new PreparedStatementSpec(sql, parameters);
    }
}
