package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Ntile;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface NtilePsStrategy {
    PsDto handle(Ntile ntile, Visitor<PsDto> visitor, AstContext ctx);
}
