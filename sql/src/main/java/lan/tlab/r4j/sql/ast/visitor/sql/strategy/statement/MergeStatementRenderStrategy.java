package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.sql.ast.dml.statement.MergeStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface MergeStatementRenderStrategy extends StatementRenderStrategy {
    String render(MergeStatement statement, SqlRenderer sqlRenderer, AstContext ctx);
}
