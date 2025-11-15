package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll;

import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.SimpleDataTypeRenderStrategy;

public class StandardSqlSimpleDataTypeRenderStrategy implements SimpleDataTypeRenderStrategy {
    @Override
    public String render(SimpleDataType type, SqlRenderer sqlRenderer, AstContext ctx) {
        return type.name();
    }
}
