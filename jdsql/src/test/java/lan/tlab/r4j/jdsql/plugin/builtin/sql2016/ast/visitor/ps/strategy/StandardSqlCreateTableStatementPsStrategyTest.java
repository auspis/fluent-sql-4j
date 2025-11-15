package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.ddl.definition.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.r4j.sql.ast.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.ddl.statement.CreateTableStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCreateTableStatementPsStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlCreateTableStatementPsStrategyTest {

    private StandardSqlCreateTableStatementPsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlCreateTableStatementPsStrategy();
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
