package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.statement.InsertStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface InsertStatementPsStrategy {
    PsDto handle(InsertStatement insertStatement, Visitor<PsDto> visitor, AstContext ctx);
}
