package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl;

import lan.tlab.r4j.jdsql.ast.ddl.definition.TableDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface TableDefinitionRenderStrategy extends SqlItemRenderStrategy {

    String render(TableDefinition item, SqlRenderer sqlRenderer, AstContext ctx);
}
