package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.TableDefinitionPsStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StandardSqlTableDefinitionPsStrategyTest {

    private final TableDefinitionPsStrategy strategy = new StandardSqlTableDefinitionPsStrategy();
    private final PreparedStatementRenderer renderer =
            PreparedStatementRenderer.builder().build();
    private final AstContext context = new AstContext();

    @Test
    void shouldHandleTableWithSingleColumn() {
        // Given
        TableDefinition tableDefinition = TableDefinition.builder()
                .table(new TableIdentifier("users"))
                .column(ColumnDefinition.builder()
                        .name("id")
                        .type(new SimpleDataType("INTEGER"))
                        .build())
                .build();

        // When
        PsDto result = strategy.handle(tableDefinition, renderer, context);

        // Then
        Assertions.assertThat(result.sql()).isEqualTo("\"users\" (\"id\" INTEGER)");
        Assertions.assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleTableWithMultipleColumns() {
        // Given
        TableDefinition tableDefinition = TableDefinition.builder()
                .table(new TableIdentifier("users"))
                .column(ColumnDefinition.builder()
                        .name("id")
                        .type(new SimpleDataType("INTEGER"))
                        .build())
                .column(ColumnDefinition.builder()
                        .name("name")
                        .type(new SimpleDataType("VARCHAR"))
                        .build())
                .build();

        // When
        PsDto result = strategy.handle(tableDefinition, renderer, context);

        // Then
        Assertions.assertThat(result.sql()).isEqualTo("\"users\" (\"id\" INTEGER, \"name\" VARCHAR)");
        Assertions.assertThat(result.parameters()).isEmpty();
    }
}
