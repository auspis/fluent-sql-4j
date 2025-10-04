package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.item.InsertData.InsertSource;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface InsertSourcePsStrategy {
    PsDto handle(InsertSource insertSource, Visitor<PsDto> visitor, AstContext ctx);
}
