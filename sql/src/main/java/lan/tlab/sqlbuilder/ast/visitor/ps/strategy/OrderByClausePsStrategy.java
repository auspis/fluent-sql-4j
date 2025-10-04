package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface OrderByClausePsStrategy {
    PsDto handle(OrderBy orderBy, Visitor<PsDto> visitor, AstContext ctx);
}
