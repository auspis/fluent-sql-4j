package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface SelectStatementPsStrategy {
    PsDto handle(SelectStatement selectStatement, Visitor<PsDto> visitor, AstContext ctx);
}
