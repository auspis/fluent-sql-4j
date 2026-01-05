package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ColumnDefinition.ColumnDefinitionBuilder;
import io.github.auspis.fluentsql4j.ast.ddl.definition.TableDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.statement.CreateTableStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlCreateTableStatementPsStrategyTest {

    private StandardSqlCreateTableStatementPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlCreateTableStatementPsStrategy();
        visitor = AstToPreparedStatementSpecVisitor.builder().build();
        ctx = new AstContext(AstContext.Feature.DDL);
    }

    @Test
    void simpleCreateTable() {
        CreateTableStatement createTable = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("users"))
                .columns(List.of(ColumnDefinitionBuilder.integer("id").build()))
                .build());

        PreparedStatementSpec result = strategy.handle(createTable, visitor, ctx);

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

        PreparedStatementSpec result = strategy.handle(createTable, visitor, ctx);

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

        PreparedStatementSpec result = strategy.handle(createTable, visitor, ctx);

        assertThat(result.sql()).startsWith("CREATE TABLE");
        assertThat(result.sql()).contains("myschema");
        assertThat(result.sql()).contains("users");
        assertThat(result.sql()).contains("id");
        assertThat(result.parameters()).isEmpty();
    }
}
