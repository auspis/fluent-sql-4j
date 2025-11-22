package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.jdsql.ast.dml.statement.UpdateStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface UpdateStatementRenderStrategy extends StatementRenderStrategy {
    String render(UpdateStatement statement, SqlRenderer sqlRenderer, AstContext ctx);
}
