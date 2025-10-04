package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.scalar.convert.Cast;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface CastPsStrategy {
    PsDto handle(Cast cast, PreparedStatementVisitor visitor, AstContext ctx);
}
