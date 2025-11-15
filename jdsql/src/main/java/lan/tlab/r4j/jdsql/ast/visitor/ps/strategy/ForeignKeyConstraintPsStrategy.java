package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface ForeignKeyConstraintPsStrategy {
    PsDto handle(ForeignKeyConstraintDefinition constraint, PreparedStatementRenderer renderer, AstContext ctx);
}
