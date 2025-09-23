package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.UnaryNumeric;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface UnaryNumericPsStrategy {

    PsDto handle(UnaryNumeric functionCall, PreparedStatementVisitor visitor, AstContext ctx);
}
