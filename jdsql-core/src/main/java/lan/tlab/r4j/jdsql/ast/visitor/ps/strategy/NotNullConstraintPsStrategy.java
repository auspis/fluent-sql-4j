package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface NotNullConstraintPsStrategy {
    PreparedStatementSpec handle(
            NotNullConstraintDefinition constraint, AstToPreparedStatementSpecVisitor renderer, AstContext ctx);
}
