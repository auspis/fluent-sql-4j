package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl;

import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface SimpleDataTypeRenderStrategy extends SqlItemRenderStrategy {

    String render(SimpleDataType type, SqlRenderer sqlRenderer, AstContext ctx);
}
