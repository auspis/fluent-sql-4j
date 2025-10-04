package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.expression.bool.Between;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface BetweenPsStrategy {
    PsDto handle(Between between, Visitor<PsDto> visitor, AstContext ctx);
}
