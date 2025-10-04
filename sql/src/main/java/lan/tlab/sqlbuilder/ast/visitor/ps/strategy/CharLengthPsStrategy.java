package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.CharLength;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface CharLengthPsStrategy {
    PsDto handle(CharLength charLength, PreparedStatementVisitor visitor, AstContext ctx);
}
