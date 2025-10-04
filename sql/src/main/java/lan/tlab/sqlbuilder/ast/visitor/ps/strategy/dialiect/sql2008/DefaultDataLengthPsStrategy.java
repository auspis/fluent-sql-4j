package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.DataLength;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.DataLengthPsStrategy;

public class DefaultDataLengthPsStrategy implements DataLengthPsStrategy {

    @Override
    public PsDto handle(DataLength dataLength, PreparedStatementVisitor visitor, AstContext ctx) {
        var expressionResult = dataLength.getExpression().accept(visitor, ctx);

        String sql = String.format("DATALENGTH(%s)", expressionResult.sql());
        return new PsDto(sql, expressionResult.parameters());
    }
}
