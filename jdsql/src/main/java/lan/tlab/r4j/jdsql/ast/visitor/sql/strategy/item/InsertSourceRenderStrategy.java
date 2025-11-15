package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertSource;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface InsertSourceRenderStrategy extends SqlItemRenderStrategy {
    String render(InsertSource item, SqlRenderer sqlRenderer, AstContext ctx);
}
