package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface SelectStatementRenderStrategy extends StatementRenderStrategy {
    String render(SelectStatement statement, SqlRenderer sqlRenderer, AstContext ctx);
}
