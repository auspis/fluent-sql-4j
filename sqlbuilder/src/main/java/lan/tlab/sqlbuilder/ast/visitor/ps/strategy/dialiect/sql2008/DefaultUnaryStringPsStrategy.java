package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.UnaryString;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.UnaryStringPsStrategy;

public class DefaultUnaryStringPsStrategy implements UnaryStringPsStrategy {

    @Override
    public PsDto handle(UnaryString functionCall, PreparedStatementVisitor visitor, AstContext ctx) {
        PsDto expressionDto = functionCall.getExpression().accept(visitor, ctx);
        String sql = String.format("%s(%s)", functionCall.getFunctionName(), expressionDto.sql());
        return new PsDto(sql, expressionDto.parameters());
    }
}
