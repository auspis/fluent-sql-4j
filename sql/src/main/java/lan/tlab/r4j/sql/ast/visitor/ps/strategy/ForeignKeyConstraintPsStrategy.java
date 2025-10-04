package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.item.ddl.Constraint.ForeignKeyConstraint;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface ForeignKeyConstraintPsStrategy {
    PsDto handle(ForeignKeyConstraint constraint, PreparedStatementVisitor visitor, AstContext ctx);
}
