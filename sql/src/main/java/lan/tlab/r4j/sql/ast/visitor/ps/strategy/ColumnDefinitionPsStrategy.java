package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface ColumnDefinitionPsStrategy {
    PsDto handle(ColumnDefinition columnDefinition, PreparedStatementRenderer renderer, AstContext ctx);
}
