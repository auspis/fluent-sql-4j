package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.RowNumber;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface RowNumberPsStrategy {
    PsDto handle(RowNumber rowNumber, Visitor<PsDto> visitor, AstContext ctx);
}
