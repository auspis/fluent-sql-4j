package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.DataLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DataLengthPsStrategy;

public class StandardSqlDataLengthPsStrategy implements DataLengthPsStrategy {

    @Override
    public PsDto handle(DataLength dataLength, PreparedStatementRenderer renderer, AstContext ctx) {
        var expressionResult = dataLength.expression().accept(renderer, ctx);

        String sql = String.format("DATALENGTH(%s)", expressionResult.sql());
        return new PsDto(sql, expressionResult.parameters());
    }
}
