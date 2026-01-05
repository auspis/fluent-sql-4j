package io.github.auspis.fluentsql4j.dsl.table;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Validation and exception handling for CreateTableBuilder fluent API.
 * Ensures all constraint methods enforce strict validation:
 * - null/empty inputs throw IllegalArgumentException
 * - Column references (notNullColumn) validate existence
 * Design: All invalid inputs are rejected immediately with clear error messages.
 */
class CreateTableBuilderNegativeBranchesTest {

    private DSL dsl;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        dsl = StandardSQLDialectPlugin.instance().createDSL();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void primaryKeyWithNoColumnsThrowsException() {
        assertThatThrownBy(() -> dsl.createTable("users")
                        .column("id")
                        .integer()
                        .primaryKey() // empty varargs → throw
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column names cannot be empty in primaryKey()");
    }

    @Test
    void indexWithNullNameThrowsException() {
        assertThatThrownBy(() -> dsl.createTable("users")
                        .column("email")
                        .varchar(255)
                        .index(null, "email") // null name → throw
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Index name cannot be null");
    }

    @Test
    void indexWithNoColumnsThrowsException() {
        assertThatThrownBy(() -> dsl.createTable("users")
                        .column("email")
                        .varchar(255)
                        .index("idx_email") // empty varargs → throw
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column names cannot be empty in index()");
    }

    @Test
    void uniqueWithNoColumnsThrowsException() {
        CreateTableBuilder builder = dsl.createTable("users");
        builder.column("email").varchar(255).buildColumn();

        assertThatThrownBy(
                        () -> builder.unique() // empty varargs → throw
                        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column names cannot be empty in unique()");
    }

    @Test
    void foreignKeyWithNullColumnThrowsException() {
        CreateTableBuilder builder = dsl.createTable("orders");
        builder.column("customer_id").integer().buildColumn();

        assertThatThrownBy(
                        () -> builder.foreignKey(null, "customer", "id") // null column → throw
                        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name cannot be null in foreignKey()");
    }

    @Test
    void foreignKeyWithNullRefTableThrowsException() {
        CreateTableBuilder builder = dsl.createTable("orders");
        builder.column("customer_id").integer().buildColumn();

        assertThatThrownBy(
                        () -> builder.foreignKey("customer_id", null, "id") // null ref table → throw
                        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Referenced table name cannot be null in foreignKey()");
    }

    @Test
    void foreignKeyWithNoRefColumnsThrowsException() {
        CreateTableBuilder builder = dsl.createTable("orders");
        builder.column("customer_id").integer().buildColumn();

        assertThatThrownBy(
                        () -> builder.foreignKey("customer_id", "customer") // empty varargs → throw
                        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Referenced column names cannot be empty in foreignKey()");
    }

    @Test
    void checkWithNullPredicateThrowsException() {
        assertThatThrownBy(() -> dsl.createTable("people")
                        .column("age")
                        .integer()
                        .check(null) // null predicate → throw
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Predicate cannot be null in check()");
    }

    @Test
    void defaultConstraintWithNullValueThrowsException() {
        CreateTableBuilder builder = dsl.createTable("settings");
        builder.column("enabled").bool().buildColumn();

        assertThatThrownBy(
                        () -> builder.defaultConstraint(null) // null value → throw
                        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Default value expression cannot be null in defaultConstraint()");
    }

    @Test
    void notNullColumnWithNullNameThrowsException() {
        CreateTableBuilder builder = dsl.createTable("users");
        builder.column("email").varchar(255);

        assertThatThrownBy(
                        () -> builder.notNullColumn(null) // null column name → throw
                        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name cannot be null or empty in notNullColumn()");
    }

    @Test
    void notNullColumnWithEmptyNameThrowsException() {
        CreateTableBuilder builder = dsl.createTable("users");
        builder.column("email").varchar(255);

        assertThatThrownBy(
                        () -> builder.notNullColumn("  ") // empty/whitespace only → throw
                        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name cannot be null or empty in notNullColumn()");
    }

    @Test
    void notNullColumnWithMissingColumnThrowsException() {
        CreateTableBuilder builder = dsl.createTable("users");
        builder.column("email").varchar(255);

        assertThatThrownBy(
                        () -> builder.notNullColumn("missing") // column does not exist → throw
                        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column not found: missing");
    }
}
