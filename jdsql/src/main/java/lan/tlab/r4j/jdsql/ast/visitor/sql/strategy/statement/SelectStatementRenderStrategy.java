package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface SelectStatementRenderStrategy extends StatementRenderStrategy {
    String render(SelectStatement statement, SqlRenderer sqlRenderer, AstContext ctx);
}
