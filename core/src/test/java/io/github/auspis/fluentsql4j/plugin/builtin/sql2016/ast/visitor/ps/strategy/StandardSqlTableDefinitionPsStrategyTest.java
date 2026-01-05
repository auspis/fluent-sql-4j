package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ColumnDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.DataType.SimpleDataType;
import io.github.auspis.fluentsql4j.ast.ddl.definition.TableDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.TableDefinitionPsStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StandardSqlTableDefinitionPsStrategyTest {

    private final TableDefinitionPsStrategy strategy = new StandardSqlTableDefinitionPsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory =
            AstToPreparedStatementSpecVisitor.builder().build();
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
