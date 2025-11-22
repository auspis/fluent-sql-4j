package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.TableDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface TableDefinitionPsStrategy {
    PsDto handle(TableDefinition tableDefinition, PreparedStatementRenderer renderer, AstContext ctx);
}
