package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.ParameterizedDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.ParameterizedDataTypeRenderStrategy;

public class StandardSqlParameterizedDataTypeRenderStrategy implements ParameterizedDataTypeRenderStrategy {
    @Override
    public String render(ParameterizedDataType type, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s(%s)",
                type.name(),
                type.parameters().stream()
                        .map(param -> param.accept(sqlRenderer, ctx))
                        .collect(Collectors.joining(", ")));
    }
}
