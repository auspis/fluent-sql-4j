package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll;

import lan.tlab.r4j.sql.ast.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.SimpleDataTypeRenderStrategy;

public class StandardSqlSimpleDataTypeRenderStrategy implements SimpleDataTypeRenderStrategy {
    @Override
    public String render(SimpleDataType type, SqlRenderer sqlRenderer, AstContext ctx) {
        return type.name();
    }
}
