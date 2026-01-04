package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.function.number.UnaryNumeric;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UnaryNumericPsStrategy;

public class StandardSqlUnaryNumericPsStrategy implements UnaryNumericPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            UnaryNumeric functionCall, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        PreparedStatementSpec expressionDto = functionCall.numericExpression().accept(astToPsSpecVisitor, ctx);
        String sql = String.format("%s(%s)", functionCall.functionName(), expressionDto.sql());
        return new PreparedStatementSpec(sql, expressionDto.parameters());
    }
}
