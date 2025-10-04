package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.CheckConstraint;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface CheckConstraintPsStrategy {
    PsDto handle(CheckConstraint constraint, PreparedStatementVisitor visitor, AstContext ctx);
}
