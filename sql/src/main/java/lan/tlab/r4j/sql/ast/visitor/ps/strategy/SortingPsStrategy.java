package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.dql.clause.Sorting;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface SortingPsStrategy {
    PsDto handle(Sorting sorting, Visitor<PsDto> visitor, AstContext ctx);
}
