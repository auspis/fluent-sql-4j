package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.UnaryString;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface UnaryStringPsStrategy {

    PreparedStatementSpec handle(UnaryString functionCall, PreparedStatementRenderer renderer, AstContext ctx);
}
