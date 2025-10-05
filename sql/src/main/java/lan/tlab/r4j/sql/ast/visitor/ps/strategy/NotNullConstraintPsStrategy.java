package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface NotNullConstraintPsStrategy {
    PsDto handle(NotNullConstraintDefinition constraint, PreparedStatementVisitor visitor, AstContext ctx);
}
