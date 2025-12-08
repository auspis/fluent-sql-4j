package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.function.datetime.ExtractDatePart;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface ExtractDatePartPsStrategy {
    PreparedStatementSpec handle(ExtractDatePart extractDatePart, PreparedStatementRenderer renderer, AstContext ctx);
}
