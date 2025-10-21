package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface DateArithmeticPsStrategy {
    PsDto handle(DateArithmetic dateArithmetic, PreparedStatementRenderer renderer, AstContext ctx);
}
