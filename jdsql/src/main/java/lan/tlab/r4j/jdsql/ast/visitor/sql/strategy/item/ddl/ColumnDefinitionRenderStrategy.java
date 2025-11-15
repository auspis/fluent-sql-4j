package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface ColumnDefinitionRenderStrategy extends SqlItemRenderStrategy {

    String render(ColumnDefinition item, SqlRenderer sqlRenderer, AstContext ctx);
}
