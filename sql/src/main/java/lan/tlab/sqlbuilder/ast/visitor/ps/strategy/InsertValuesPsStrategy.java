package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertValues;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface InsertValuesPsStrategy {
    PsDto handle(InsertValues insertValues, Visitor<PsDto> visitor, AstContext ctx);
}
