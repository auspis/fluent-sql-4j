package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.DateArithmetic;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface DateArithmeticPsStrategy {
    PreparedStatementSpec handle(DateArithmetic dateArithmetic, PreparedStatementRenderer renderer, AstContext ctx);
}
