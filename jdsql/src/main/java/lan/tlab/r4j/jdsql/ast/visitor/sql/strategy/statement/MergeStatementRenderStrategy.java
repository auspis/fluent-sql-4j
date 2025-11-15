package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement;

import lan.tlab.r4j.jdsql.ast.dml.statement.MergeStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface MergeStatementRenderStrategy extends StatementRenderStrategy {
    String render(MergeStatement statement, SqlRenderer sqlRenderer, AstContext ctx);
}
