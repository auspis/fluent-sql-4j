package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface SelectClausePsStrategy {
    PsDto handle(Select select, Visitor<PsDto> visitor, AstContext ctx);
}
