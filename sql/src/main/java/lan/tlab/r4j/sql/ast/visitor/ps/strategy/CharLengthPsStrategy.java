package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.CharLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface CharLengthPsStrategy {
    PsDto handle(CharLength charLength, PreparedStatementVisitor visitor, AstContext ctx);
}
