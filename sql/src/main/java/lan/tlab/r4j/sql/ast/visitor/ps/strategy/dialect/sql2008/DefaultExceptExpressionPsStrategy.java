package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.set.ExceptExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ExceptExpressionPsStrategy;

public class DefaultExceptExpressionPsStrategy implements ExceptExpressionPsStrategy {

    @Override
    public PsDto handle(ExceptExpression expression, PreparedStatementVisitor visitor, AstContext ctx) {
        PsDto leftDto = expression.getLeft().accept(visitor, ctx);
        PsDto rightDto = expression.getRight().accept(visitor, ctx);

        String sql = String.format(
                "((%s) %s (%s))", leftDto.sql(), expression.isDistinct() ? "EXCEPT" : "EXCEPT ALL", rightDto.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(leftDto.parameters());
        parameters.addAll(rightDto.parameters());

        return new PsDto(sql, parameters);
    }
}
