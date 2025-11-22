package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface UniqueConstraintPsStrategy {
    PsDto handle(UniqueConstraintDefinition constraint, PreparedStatementRenderer renderer, AstContext ctx);
}
