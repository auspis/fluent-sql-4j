package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface PrimaryKeyPsStrategy {
    PsDto handle(PrimaryKeyDefinition item, PreparedStatementVisitor visitor, AstContext ctx);
}
