package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.statement.CreateTableStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface CreateTableStatementPsStrategy {
    PsDto handle(CreateTableStatement createTableStatement, PreparedStatementVisitor visitor, AstContext ctx);
}
