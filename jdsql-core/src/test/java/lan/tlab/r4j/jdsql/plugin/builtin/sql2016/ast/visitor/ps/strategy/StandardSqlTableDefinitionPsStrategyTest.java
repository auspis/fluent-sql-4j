package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.jdsql.ast.ddl.definition.TableDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.TableDefinitionPsStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StandardSqlTableDefinitionPsStrategyTest {

    private final TableDefinitionPsStrategy strategy = new StandardSqlTableDefinitionPsStrategy();
    private final PreparedStatementRenderer specFactory =
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
        PreparedStatementSpec result = strategy.handle(tableDefinition, specFactory, context);

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
        PreparedStatementSpec result = strategy.handle(tableDefinition, specFactory, context);

        // Then
        Assertions.assertThat(result.sql()).isEqualTo("\"users\" (\"id\" INTEGER, \"name\" VARCHAR)");
        Assertions.assertThat(result.parameters()).isEmpty();
    }
}
