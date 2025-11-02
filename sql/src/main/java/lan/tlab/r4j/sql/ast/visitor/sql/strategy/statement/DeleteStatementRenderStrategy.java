package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.sql.ast.statement.dml.DeleteStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface DeleteStatementRenderStrategy extends StatementRenderStrategy {
    String render(DeleteStatement statement, SqlRenderer sqlRenderer, AstContext ctx);
}
