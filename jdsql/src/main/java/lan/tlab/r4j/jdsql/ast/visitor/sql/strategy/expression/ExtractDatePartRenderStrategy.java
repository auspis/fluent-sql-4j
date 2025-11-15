package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.ExtractDatePart;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface ExtractDatePartRenderStrategy extends ExpressionRenderStrategy {

    String render(ExtractDatePart functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
