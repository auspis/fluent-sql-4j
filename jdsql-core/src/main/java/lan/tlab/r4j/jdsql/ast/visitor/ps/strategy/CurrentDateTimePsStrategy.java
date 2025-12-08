package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDateTime;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface CurrentDateTimePsStrategy {
    PreparedStatementSpec handle(CurrentDateTime currentDateTime, PreparedStatementRenderer renderer, AstContext ctx);
}
