package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface ColumnDefinitionRenderStrategy extends SqlItemRenderStrategy {

    String render(ColumnDefinition item, SqlRenderer sqlRenderer, AstContext ctx);
}
