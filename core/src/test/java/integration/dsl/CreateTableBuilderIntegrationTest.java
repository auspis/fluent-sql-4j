package integration.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.plugin.util.StandardSqlUtil;
import lan.tlab.r4j.jdsql.test.util.TestDatabaseUtil;
import lan.tlab.r4j.jdsql.test.util.annotation.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for CreateTableBuilder with H2 in-memory database.
 * Tests the complete integration between DSL CreateTableBuilder, SQL rendering,
 * PreparedStatement creation, and actual database table creation.
 */
@IntegrationTest
class CreateTableBuilderIntegrationTest {

    private Connection connection;
    private DSL dsl;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        dsl = StandardSqlUtil.dsl();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void simpleTableWithPrimaryKey() throws SQLException {
        try (PreparedStatement ps = dsl.createTable("test_users")
                .column("id")
                .integer()
                .notNull()
                .column("name")
                .varchar(100)
                .primaryKey("id")
                .build(connection)) {
            ps.execute();
        }

        assertThat(tableExists("test_users")).isTrue();
        assertThat(columnExists("test_users", "id")).isTrue();
        assertThat(columnExists("test_users", "name")).isTrue();
    }

    @Test
    void tableWithMultipleColumns() throws SQLException {
        try (PreparedStatement ps = dsl.createTable("products")
                .column("id")
                .integer()
                .notNull()
                .column("name")
                .varchar(100)
                .notNull()
                .column("price")
                .decimal(10, 2)
                .column("quantity")
                .integer()
                .primaryKey("id")
                .build(connection)) {
            ps.execute();
        }

        assertThat(tableExists("products")).isTrue();
        assertThat(columnExists("products", "id")).isTrue();
        assertThat(columnExists("products", "name")).isTrue();
        assertThat(columnExists("products", "price")).isTrue();
        assertThat(columnExists("products", "quantity")).isTrue();
    }

    @Test
    void tableWithDifferentDataTypes() throws SQLException {
        try (PreparedStatement ps = dsl.createTable("mixed_types")
                .column("id")
                .integer()
                .notNull()
                .column("active")
                .bool()
                .column("created_at")
                .timestamp()
                .column("birth_date")
                .date()
                .primaryKey("id")
                .build(connection)) {
            ps.execute();
        }

        assertThat(tableExists("mixed_types")).isTrue();
        assertThat(columnExists("mixed_types", "id")).isTrue();
        assertThat(columnExists("mixed_types", "active")).isTrue();
        assertThat(columnExists("mixed_types", "created_at")).isTrue();
        assertThat(columnExists("mixed_types", "birth_date")).isTrue();
    }

    @Test
    void tableWithCompositePrimaryKey() throws SQLException {
        try (PreparedStatement ps = dsl.createTable("order_items")
                .column("order_id")
                .integer()
                .notNull()
                .column("item_id")
                .integer()
                .notNull()
                .column("quantity")
                .integer()
                .primaryKey("order_id", "item_id")
                .build(connection)) {
            ps.execute();
        }

        assertThat(tableExists("order_items")).isTrue();
        assertThat(columnExists("order_items", "order_id")).isTrue();
        assertThat(columnExists("order_items", "item_id")).isTrue();
        assertThat(columnExists("order_items", "quantity")).isTrue();
    }

    @Test
    void convenienceMethodColumnIntegerPrimaryKey() throws SQLException {
        try (PreparedStatement ps =
                dsl.createTable("simple").columnIntegerPrimaryKey("id").build(connection)) {
            ps.execute();
        }

        assertThat(tableExists("simple")).isTrue();
        assertThat(columnExists("simple", "id")).isTrue();
    }

    @Test
    void convenienceMethodColumnVarcharNotNull() throws SQLException {
        try (PreparedStatement ps = dsl.createTable("text_table")
                .columnIntegerPrimaryKey("id")
                .columnVarcharNotNull("name", 100)
                .build(connection)) {
            ps.execute();
        }

        assertThat(tableExists("text_table")).isTrue();
        assertThat(columnExists("text_table", "id")).isTrue();
        assertThat(columnExists("text_table", "name")).isTrue();
    }

    @Test
    void convenienceMethodColumnTimestampNotNull() throws SQLException {
        try (PreparedStatement ps = dsl.createTable("timestamps")
                .columnIntegerPrimaryKey("id")
                .columnTimestampNotNull("created_at")
                .build(connection)) {
            ps.execute();
        }

        assertThat(tableExists("timestamps")).isTrue();
        assertThat(columnExists("timestamps", "id")).isTrue();
        assertThat(columnExists("timestamps", "created_at")).isTrue();
    }

    @Test
    void insertDataAfterTableCreation() throws SQLException {
        try (PreparedStatement ps = dsl.createTable("test_insert")
                .column("id")
                .integer()
                .notNull()
                .column("name")
                .varchar(50)
                .primaryKey("id")
                .build(connection)) {
            ps.execute();
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO test_insert (id, name) VALUES (1, 'Test')");

            try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM test_insert WHERE id = 1")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("id")).isEqualTo(1);
                assertThat(rs.getString("name")).isEqualTo("Test");
            }
        }
    }

    private boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        // Try both upper and lower case since H2 with DATABASE_TO_LOWER=TRUE stores tables in lowercase
        try (ResultSet rs = metaData.getTables(null, null, tableName.toLowerCase(), new String[] {"TABLE"})) {
            if (rs.next()) {
                return true;
            }
        }
        try (ResultSet rs = metaData.getTables(null, null, tableName.toUpperCase(), new String[] {"TABLE"})) {
            return rs.next();
        }
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        // Try both upper and lower case
        try (ResultSet rs = metaData.getColumns(null, null, tableName.toLowerCase(), columnName.toLowerCase())) {
            if (rs.next()) {
                return true;
            }
        }
        try (ResultSet rs = metaData.getColumns(null, null, tableName.toUpperCase(), columnName.toUpperCase())) {
            return rs.next();
        }
    }
}
