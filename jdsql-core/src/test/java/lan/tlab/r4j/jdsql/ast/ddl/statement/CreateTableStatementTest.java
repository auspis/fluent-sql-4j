package lan.tlab.r4j.jdsql.ast.ddl.statement;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType;
import lan.tlab.r4j.jdsql.ast.ddl.definition.TableDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test to verify that DDL context causes literals to be rendered inline
 * instead of as placeholders.
 */
class CreateTableStatementTest {

    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() {
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
    }

    @Test
    void ddlContextRendersLiteralsInline() {
        ColumnDefinition idCol = ColumnDefinition.builder()
                .name("id")
                .type(DataType.integer())
                .notNullConstraint(new NotNullConstraintDefinition())
                .build();

        ColumnDefinition nameCol = ColumnDefinition.builder()
                .name("name")
                .type(DataType.varchar(100))
                .build();

        TableDefinition tableDef = TableDefinition.builder()
                .name("test_users")
                .columns(List.of(idCol, nameCol))
                .primaryKey(new PrimaryKeyDefinition(List.of("id")))
                .build();

        CreateTableStatement stmt = new CreateTableStatement(tableDef);
        PreparedStatementSpec result = specFactory.create(stmt);

        // Verify that VARCHAR(100) is rendered with literal 100, not placeholder
        assertThat(result.sql()).contains("VARCHAR(100)");
        assertThat(result.sql()).doesNotContain("VARCHAR(?)");

        // Verify no parameters are in the list
        assertThat(result.parameters()).isEmpty();

        // Verify complete SQL structure
        assertThat(result.sql())
                .isEqualTo(
                        "CREATE TABLE \"test_users\" (\"id\" INTEGER NOT NULL, \"name\" VARCHAR(100), PRIMARY KEY (\"id\"))");
    }

    @Test
    void ddlContextWithDecimal() {
        ColumnDefinition priceCol = ColumnDefinition.builder()
                .name("price")
                .type(DataType.decimal(10, 2))
                .build();

        TableDefinition tableDef = TableDefinition.builder()
                .name("products")
                .columns(List.of(priceCol))
                .build();

        CreateTableStatement stmt = new CreateTableStatement(tableDef);
        PreparedStatementSpec result = specFactory.create(stmt);

        // Verify that DECIMAL(10, 2) is rendered with literals, not placeholders
        assertThat(result.sql()).contains("DECIMAL(10, 2)");
        assertThat(result.sql()).doesNotContain("DECIMAL(?, ?)");
        assertThat(result.parameters()).isEmpty();
    }
}
