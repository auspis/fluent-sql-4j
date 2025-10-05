package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultTableDefinitionPsStrategyTest {

    private final DefaultTableDefinitionPsStrategy strategy = new DefaultTableDefinitionPsStrategy();
    private final PreparedStatementVisitor visitor =
            PreparedStatementVisitor.builder().build();
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
        PsDto result = strategy.handle(tableDefinition, visitor, context);

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
        PsDto result = strategy.handle(tableDefinition, visitor, context);

        // Then
        Assertions.assertThat(result.sql()).isEqualTo("\"users\" (\"id\" INTEGER, \"name\" VARCHAR)");
        Assertions.assertThat(result.parameters()).isEmpty();
    }
}
