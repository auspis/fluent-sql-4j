package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ColumnDefinitionPsStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StandardSqlColumnDefinitionPsStrategyTest {

    private final ColumnDefinitionPsStrategy strategy = new StandardSqlColumnDefinitionPsStrategy();
    private final PreparedStatementRenderer renderer =
            PreparedStatementRenderer.builder().build();
    private final AstContext context = new AstContext();

    @Test
    void shouldHandleNullObject() {
        ColumnDefinition nullColumnDefinition = ColumnDefinition.nullObject();

        PsDto result = strategy.handle(nullColumnDefinition, renderer, context);

        Assertions.assertThat(result.sql()).isEmpty();
        Assertions.assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleSimpleColumn() {
        ColumnDefinition columnDefinition = ColumnDefinition.builder()
                .name("id")
                .type(new SimpleDataType("INTEGER"))
                .build();

        PsDto result = strategy.handle(columnDefinition, renderer, context);

        Assertions.assertThat(result.sql()).isEqualTo("\"id\" INTEGER");
        Assertions.assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleColumnWithDifferentDataType() {
        ColumnDefinition columnDefinition = ColumnDefinition.builder()
                .name("name")
                .type(new SimpleDataType("VARCHAR"))
                .build();

        PsDto result = strategy.handle(columnDefinition, renderer, context);

        Assertions.assertThat(result.sql()).isEqualTo("\"name\" VARCHAR");
        Assertions.assertThat(result.parameters()).isEmpty();
    }
}
