package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.dll;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.SimpleDataType;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class SimpleDataTypeRenderStrategy implements SqlItemRenderStrategy {

    public String render(SimpleDataType type, SqlRenderer sqlRenderer, AstContext ctx) {
        return type.getName();
    }
}
