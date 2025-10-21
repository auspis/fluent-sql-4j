package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.statement.ddl.CreateTableStatement;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultCreateTableStatementPsStrategyTest {

    private DefaultCreateTableStatementPsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new DefaultCreateTableStatementPsStrategy();
        visitor = PreparedStatementRenderer.builder().build();
        ctx = new AstContext();
    }

    @Test
    void simpleCreateTable() {
        CreateTableStatement createTable = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("users"))
                .columns(List.of(ColumnDefinitionBuilder.integer("id").build()))
                .build());

        PsDto result = strategy.handle(createTable, visitor, ctx);

        assertThat(result.sql()).startsWith("CREATE TABLE");
        assertThat(result.sql()).contains("users");
        assertThat(result.sql()).contains("id");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void createTableWithMultipleColumns() {
        CreateTableStatement createTable = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("users"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build()))
                .build());

        PsDto result = strategy.handle(createTable, visitor, ctx);

        assertThat(result.sql()).startsWith("CREATE TABLE");
        assertThat(result.sql()).contains("users");
        assertThat(result.sql()).contains("id");
        assertThat(result.sql()).contains("name");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void createTableWithSchema() {
        CreateTableStatement createTable = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("myschema", "users"))
                .columns(List.of(ColumnDefinitionBuilder.integer("id").build()))
                .build());

        PsDto result = strategy.handle(createTable, visitor, ctx);

        assertThat(result.sql()).startsWith("CREATE TABLE");
        assertThat(result.sql()).contains("myschema");
        assertThat(result.sql()).contains("users");
        assertThat(result.sql()).contains("id");
        assertThat(result.parameters()).isEmpty();
    }
}
