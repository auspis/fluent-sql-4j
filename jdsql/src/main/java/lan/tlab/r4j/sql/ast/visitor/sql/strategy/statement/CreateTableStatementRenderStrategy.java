package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.sql.ast.ddl.statement.CreateTableStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface CreateTableStatementRenderStrategy extends StatementRenderStrategy {

    String render(CreateTableStatement statement, SqlRenderer sqlRenderer, AstContext ctx);
}
