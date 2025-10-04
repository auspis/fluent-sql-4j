package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface CurrentDateTimePsStrategy {
    PsDto handle(CurrentDateTime currentDateTime, PreparedStatementVisitor visitor, AstContext ctx);
}
