package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.SimpleDataType;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultColumnDefinitionPsStrategyTest {

    private final DefaultColumnDefinitionPsStrategy strategy = new DefaultColumnDefinitionPsStrategy();
    private final PreparedStatementVisitor visitor =
            PreparedStatementVisitor.builder().build();
    private final AstContext context = new AstContext();

    @Test
    void shouldHandleNullObject() {
        // Given
        ColumnDefinition nullColumnDefinition = ColumnDefinition.nullObject();

        // When
        PsDto result = strategy.handle(nullColumnDefinition, visitor, context);

        // Then
        Assertions.assertThat(result.sql()).isEmpty();
        Assertions.assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleSimpleColumn() {
        // Given
        ColumnDefinition columnDefinition = ColumnDefinition.builder()
                .name("id")
                .type(new SimpleDataType("INTEGER"))
                .build();

        // When
        PsDto result = strategy.handle(columnDefinition, visitor, context);

        // Then
        Assertions.assertThat(result.sql()).isEqualTo("\"id\" INTEGER");
        Assertions.assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleColumnWithDifferentDataType() {
        // Given
        ColumnDefinition columnDefinition = ColumnDefinition.builder()
                .name("name")
                .type(new SimpleDataType("VARCHAR"))
                .build();

        // When
        PsDto result = strategy.handle(columnDefinition, visitor, context);

        // Then
        Assertions.assertThat(result.sql()).isEqualTo("\"name\" VARCHAR");
        Assertions.assertThat(result.parameters()).isEmpty();
    }
}
