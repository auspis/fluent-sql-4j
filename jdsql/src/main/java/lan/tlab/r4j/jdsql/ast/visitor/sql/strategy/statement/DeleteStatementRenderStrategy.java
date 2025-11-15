package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.jdsql.ast.dml.statement.DeleteStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface DeleteStatementRenderStrategy extends StatementRenderStrategy {
    String render(DeleteStatement statement, SqlRenderer sqlRenderer, AstContext ctx);
}
