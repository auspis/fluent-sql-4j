package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface UniqueConstraintPsStrategy {
    PreparedStatementSpec handle(
            UniqueConstraintDefinition constraint,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx);
}
