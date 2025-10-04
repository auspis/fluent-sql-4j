package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.clause.from.source.FromSubquery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface FromSubqueryPsStrategy {
    PsDto handle(FromSubquery fromSubquery, Visitor<PsDto> visitor, AstContext ctx);
}
