package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.statement.CreateTableStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface CreateTableStatementPsStrategy {
    PsDto handle(CreateTableStatement createTableStatement, PreparedStatementRenderer renderer, AstContext ctx);
}
