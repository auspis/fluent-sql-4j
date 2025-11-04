package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.IndexDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface IndexDefinitionRenderStrategy extends SqlItemRenderStrategy {

    String render(IndexDefinition indexDefinition, SqlRenderer sqlRenderer, AstContext ctx);
}
