package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.set.UnionExpression;
import lan.tlab.r4j.sql.ast.expression.set.UnionExpression.UnionType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UnionExpressionPsStrategy;

public class DefaultUnionExpressionPsStrategy implements UnionExpressionPsStrategy {
    @Override
    public PsDto handle(UnionExpression expression, PreparedStatementVisitor visitor, AstContext ctx) {

        PsDto leftPart = expression.getLeft().accept(visitor, ctx);
        PsDto rightPart = expression.getRight().accept(visitor, ctx);

        String sql = String.format(
                "((%s) %s (%s))",
                leftPart.sql(),
                (expression.getType() == UnionType.UNION_DISTINCT ? "UNION" : "UNION ALL"),
                rightPart.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(leftPart.parameters());
        parameters.addAll(rightPart.parameters());

        return new PsDto(sql, parameters);
    }
}
