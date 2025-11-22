package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dml.statement.DeleteStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface DeleteStatementPsStrategy {
    PsDto handle(DeleteStatement deleteStatement, Visitor<PsDto> visitor, AstContext ctx);
}
