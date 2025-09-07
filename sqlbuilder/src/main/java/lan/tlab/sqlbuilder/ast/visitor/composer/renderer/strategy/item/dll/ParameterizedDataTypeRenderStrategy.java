package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.ParameterizedDataType;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.SqlItemRenderStrategy;

public class ParameterizedDataTypeRenderStrategy implements SqlItemRenderStrategy {

    public String render(ParameterizedDataType type, SqlRenderer sqlRenderer) {
        return String.format(
                "%s(%s)",
                type.getName(),
                type.getParameters().stream()
                        .map(param -> param.accept(sqlRenderer))
                        .collect(Collectors.joining(", ")));
    }
}
