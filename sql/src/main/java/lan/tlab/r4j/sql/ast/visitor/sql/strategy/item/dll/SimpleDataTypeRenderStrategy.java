package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class SimpleDataTypeRenderStrategy implements SqlItemRenderStrategy {

    public String render(SimpleDataType type, SqlRenderer sqlRenderer, AstContext ctx) {
        return type.getName();
    }
}
