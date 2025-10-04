package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.clause.groupby.GroupBy;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface GroupByClausePsStrategy {
    PsDto handle(GroupBy groupBy, Visitor<PsDto> visitor, AstContext ctx);
}
