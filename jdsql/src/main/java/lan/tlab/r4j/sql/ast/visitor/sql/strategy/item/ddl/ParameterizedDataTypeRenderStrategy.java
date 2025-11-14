package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl;

import lan.tlab.r4j.sql.ast.ddl.definition.DataType.ParameterizedDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface ParameterizedDataTypeRenderStrategy extends SqlItemRenderStrategy {

    String render(ParameterizedDataType type, SqlRenderer sqlRenderer, AstContext ctx);
}
