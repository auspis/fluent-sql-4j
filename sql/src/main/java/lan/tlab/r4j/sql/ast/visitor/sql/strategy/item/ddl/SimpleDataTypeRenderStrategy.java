package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface SimpleDataTypeRenderStrategy extends SqlItemRenderStrategy {

    String render(SimpleDataType type, SqlRenderer sqlRenderer, AstContext ctx);
}
