package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Left;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LeftPsStrategy;

public class StandardSqlLeftPsStrategy implements LeftPsStrategy {

    @Override
    public PsDto handle(Left left, PreparedStatementRenderer renderer, AstContext ctx) {
        var expressionResult = left.expression().accept(renderer, ctx);
        var lengthResult = left.length().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(expressionResult.parameters());
        parameters.addAll(lengthResult.parameters());

        String sql = String.format("LEFT(%s, %s)", expressionResult.sql(), lengthResult.sql());
        return new PsDto(sql, parameters);
    }
}
