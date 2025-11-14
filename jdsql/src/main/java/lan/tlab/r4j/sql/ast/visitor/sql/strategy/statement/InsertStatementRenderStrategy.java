package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.sql.ast.dml.statement.InsertStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface InsertStatementRenderStrategy extends StatementRenderStrategy {
    String render(InsertStatement statement, SqlRenderer sqlRenderer, AstContext ctx);
}
