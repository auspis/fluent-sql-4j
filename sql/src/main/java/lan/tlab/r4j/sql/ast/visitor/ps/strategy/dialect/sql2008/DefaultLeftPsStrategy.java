package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Left;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LeftPsStrategy;

public class DefaultLeftPsStrategy implements LeftPsStrategy {

    @Override
    public PsDto handle(Left left, PreparedStatementVisitor visitor, AstContext ctx) {
        var expressionResult = left.getExpression().accept(visitor, ctx);
        var lengthResult = left.getLength().accept(visitor, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(expressionResult.parameters());
        parameters.addAll(lengthResult.parameters());

        String sql = String.format("LEFT(%s, %s)", expressionResult.sql(), lengthResult.sql());
        return new PsDto(sql, parameters);
    }
}
