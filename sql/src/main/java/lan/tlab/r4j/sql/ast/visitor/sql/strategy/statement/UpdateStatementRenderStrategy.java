package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.sql.ast.statement.dml.UpdateStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface UpdateStatementRenderStrategy extends StatementRenderStrategy {
    String render(UpdateStatement statement, SqlRenderer sqlRenderer, AstContext ctx);
}
