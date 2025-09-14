package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.SimpleDataType;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.SqlItemRenderStrategy;

public class SimpleDataTypeRenderStrategy implements SqlItemRenderStrategy {

    public String render(SimpleDataType type, SqlRenderer sqlRenderer, AstContext ctx) {
        return type.getName();
    }
}
