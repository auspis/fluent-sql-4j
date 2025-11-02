package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.set.UnionExpression;
import lan.tlab.r4j.sql.ast.expression.set.UnionExpression.UnionType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UnionExpressionPsStrategy;

public class StandardSqlUnionExpressionPsStrategy implements UnionExpressionPsStrategy {
    @Override
    public PsDto handle(UnionExpression expression, PreparedStatementRenderer renderer, AstContext ctx) {

        PsDto leftPart = expression.left().accept(renderer, ctx);
        PsDto rightPart = expression.right().accept(renderer, ctx);

        String sql = String.format(
                "((%s) %s (%s))",
                leftPart.sql(),
                (expression.type() == UnionType.UNION_DISTINCT ? "UNION" : "UNION ALL"),
                rightPart.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(leftPart.parameters());
        parameters.addAll(rightPart.parameters());

        return new PsDto(sql, parameters);
    }
}
