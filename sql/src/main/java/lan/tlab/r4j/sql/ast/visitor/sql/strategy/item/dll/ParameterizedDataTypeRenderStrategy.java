package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.expression.item.ddl.DataType.ParameterizedDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class ParameterizedDataTypeRenderStrategy implements SqlItemRenderStrategy {

    public String render(ParameterizedDataType type, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s(%s)",
                type.getName(),
                type.getParameters().stream()
                        .map(param -> param.accept(sqlRenderer, ctx))
                        .collect(Collectors.joining(", ")));
    }
}
