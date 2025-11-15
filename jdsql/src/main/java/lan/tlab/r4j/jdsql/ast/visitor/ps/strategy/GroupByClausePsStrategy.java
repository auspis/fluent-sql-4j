package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface GroupByClausePsStrategy {
    PsDto handle(GroupBy groupBy, Visitor<PsDto> visitor, AstContext ctx);
}
