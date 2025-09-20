package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface GroupByClausePsStrategy {
    PsDto handle(GroupBy groupBy, Visitor<PsDto> visitor, AstContext ctx);
}
