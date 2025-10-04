package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.item.ddl.Constraint.CheckConstraint;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface CheckConstraintPsStrategy {
    PsDto handle(CheckConstraint constraint, PreparedStatementVisitor visitor, AstContext ctx);
}
