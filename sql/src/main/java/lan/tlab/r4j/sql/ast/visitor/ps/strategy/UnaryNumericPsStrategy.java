package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.UnaryNumeric;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface UnaryNumericPsStrategy {

    PsDto handle(UnaryNumeric functionCall, PreparedStatementVisitor visitor, AstContext ctx);
}
