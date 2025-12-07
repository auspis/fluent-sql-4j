package e2e.system;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.dsl.DSLRegistry;
import lan.tlab.r4j.jdsql.dsl.select.SelectBuilder;
import lan.tlab.r4j.jdsql.dsl.util.ResultSetUtil;
import lan.tlab.r4j.jdsql.dsl.util.ResultSetUtil.RowMapper;
import lan.tlab.r4j.jdsql.functional.Result;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.MysqlDialectPlugin;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.dsl.MysqlDSL;
import lan.tlab.r4j.jdsql.test.util.TestDatabaseUtil;
import lan.tlab.r4j.jdsql.test.util.annotation.E2ETest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-end tests for MysqlDSLE2E with DSLRegistry and real MySQL database.
 * <p>
 * These tests verify that the plugin integrates correctly with the registry,
 * is discoverable via ServiceLoader, and produces functional SQL renderers
 * that work with real MySQL database operations using Testcontainers.
 */
@E2ETest
@Testcontainers
class MysqlDSLE2E {

    @Container
    @SuppressWarnings("resource")
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static DSLRegistry registry;
    private static Connection connection;
    private static RowMapper<String> nameMapper;

    private MysqlDSL dsl;

    @BeforeAll
    static void beforeAll() throws Exception {
        registry = DSLRegistry.createWithServiceLoader();

        connection = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());

        TestDatabaseUtil.dropUsersTable(connection);
        TestDatabaseUtil.createUsersTableWithBackTicks(connection);

        nameMapper = r -> r.getString("name");
    }

    @AfterAll
    static void afterAll() throws Exception {
        connection.close();
    }

    @BeforeEach
    void setUp() throws SQLException {
        dsl = (MysqlDSL) registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, MysqlDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
        TestDatabaseUtil.truncateUsers(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
    }

    @Test
    void pluginDiscovery_ok() {
        Result<DSL> dslResult = registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, MysqlDialectPlugin.DIALECT_VERSION);
        assertThat(dslResult).isInstanceOf(Result.Success.class);
        assertThat(dslResult.orElseThrow()).isNotNull().isInstanceOf(MysqlDSL.class);
    }

    @Test
    void select() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("users", "name")
                .column("users", "email")
                .from("users")
                .where()
                .column("email")
                .eq("john@example.com")
                .buildPreparedStatement(connection);

        assertThat(ResultSetUtil.list(ps, nameMapper)).hasSize(1).containsAll(List.of("John Doe"));
    }

    @Test
    void pagination() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("users", "name")
                .column("users", "email")
                .from("users")
                .orderBy("name")
                .fetch(3)
                .offset(1)
                .buildPreparedStatement(connection);

        assertThat(ResultSetUtil.list(ps, nameMapper)).hasSize(3).containsAll(List.of("Bob", "Charlie", "Diana"));
    }

    @Test
    void mergeStatementWithRealDatabase() throws SQLException {
        TestDatabaseUtil.createUsersUpdatesTableWithRecords(connection);
        // Build and execute MERGE statement using DSL
        try (var ps = dsl.mergeInto("users")
                .as("tgt")
                .using("users_updates", "src")
                .on("tgt.id", "src.id")
                .whenMatched()
                .set("name", "src.name")
                .set("email", "src.email")
                .set("age", "src.age")
                .set("active", "src.active")
                .set("birthdate", "src.birthdate")
                .set("createdAt", "src.createdAt")
                .set("address", "src.address")
                .set("preferences", "src.preferences")
                .whenNotMatched()
                .set("id", "src.id")
                .set("name", "src.name")
                .set("email", "src.email")
                .set("age", "src.age")
                .set("active", "src.active")
                .set("birthdate", "src.birthdate")
                .set("createdAt", "src.createdAt")
                .set("address", "src.address")
                .set("preferences", "src.preferences")
                .buildPreparedStatement(connection)) {
            int affectedRows = ps.executeUpdate();
            // MySQL ON DUPLICATE KEY UPDATE returns affected rows count
            // 1 = inserted, 2 = updated (technically 1 deleted + 1 inserted in MySQL's logic)
            assertThat(affectedRows).isGreaterThan(0);
        }

        // Verify John Doe was updated
        try (var ps = connection.prepareStatement("SELECT * FROM users WHERE id = 1");
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(1);
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getString("email")).isEqualTo("john.newemail@example.com");
            assertThat(rs.getInt("age")).isEqualTo(31);
            assertThat(rs.getBoolean("active")).isTrue();
        }

        // Verify new user was inserted
        try (var ps = connection.prepareStatement("SELECT * FROM users WHERE id = 11");
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(11);
            assertThat(rs.getString("name")).isEqualTo("New User");
            assertThat(rs.getString("email")).isEqualTo("newuser@example.com");
            assertThat(rs.getInt("age")).isEqualTo(28);
            assertThat(rs.getBoolean("active")).isTrue();
        }

        // Verify total count (original 10 + 1 new = 11)
        try (var ps = connection.prepareStatement("SELECT COUNT(*) as cnt FROM users");
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("cnt")).isEqualTo(11);
        }
    }

    @Test
    void groupConcatFunction() throws SQLException {
        RowMapper<List<String>> mapper = r -> List.of(r.getString("names"), r.getString("age"));
        List<List<String>> expected = List.of(
                List.of("Bob", "15"),
                List.of("Jane Smith, Diana", "25"),
                List.of("Grace", "28"),
                List.of("John Doe, Charlie, Henry", "30"),
                List.of("Alice, Frank", "35"),
                List.of("Eve", "40"));
        SelectBuilder selectBuilder = dsl.select()
                .column("age")
                .groupConcat("name")
                .separator(", ")
                .as("names")
                .from("users")
                .groupBy("age")
                .orderBy("age");

        // prepared statement
        PreparedStatement ps = selectBuilder.buildPreparedStatement(connection);
        assertThat(ResultSetUtil.list(ps, mapper)).containsAll(expected);
    }

    @Test
    void ifFunction() throws SQLException {
        RowMapper<List<String>> mapper =
                r -> List.of(r.getString("name"), r.getString("age"), r.getString("age_group"));
        List<List<String>> expected = List.of(
                List.of("Alice", "35", "adult"),
                List.of("Bob", "15", "minor"),
                List.of("Charlie", "30", "adult"),
                List.of("Diana", "25", "adult"),
                List.of("Eve", "40", "adult"),
                List.of("Frank", "35", "adult"),
                List.of("Grace", "28", "adult"),
                List.of("Henry", "30", "adult"),
                List.of("Jane Smith", "25", "adult"),
                List.of("John Doe", "30", "adult"));
        SelectBuilder selectBuilder = dsl.select()
                .column("name")
                .column("age")
                .ifExpr()
                .when("users", "age")
                .gte(18)
                .then("adult")
                .otherwise("minor")
                .as("age_group")
                .from("users")
                .orderBy("name");

        // prepared statement
        PreparedStatement ps = selectBuilder.buildPreparedStatement(connection);
        assertThat(ResultSetUtil.list(ps, mapper)).containsAll(expected);
    }

    //  @Test
    //  void shouldExecuteMySQLGroupConcatFunctionWithPreparedStatement() throws SQLException {
    //      // Get DSL from registry
    //      var dsl = (MysqlDSL) pluginRegistry.getDsl(MysqlDialectPlugin.DIALECT_NAME, "8.0.35").orElseThrow();
    //
    //      // Use GROUP_CONCAT to concatenate names by age group
    //      var selectBuilder = dsl.select()
    //              .column("age")
    //              .groupConcat("name")
    //              .separator(", ")
    //              .as("names")
    //              .from("users")
    //              .groupBy("age")
    //              .orderBy("age");
    //
    //      // Build prepared statement
    //      PreparedStatement ps = selectBuilder.buildPreparedStatement(connection);
    //
    //      // Execute and verify results
    //      try (var rs = ps.executeQuery()) {
    //          boolean found30YearOlds = false;
    //          while (rs.next()) {
    //              int age = rs.getInt("age");
    //              String names = rs.getString("names");
    //              assertThat(names).isNotNull();
    //
    //              // Verify 30-year-olds are concatenated
    //              if (age == 30) {
    //                  found30YearOlds = true;
    //                  // Should contain multiple names separated by ", "
    //                  assertThat(names).contains(", ");
    //                  assertThat(names.split(", ")).hasSizeGreaterThanOrEqualTo(2);
    //              }
    //          }
    //          assertThat(found30YearOlds).isTrue();
    //      } finally {
    //          ps.close();
    //      }
    //  }
    //
    //  @Test
    //  void shouldExecuteMySQLDateFormatFunction() throws SQLException {
    //      // Get DSL from registry
    //      var dsl = (MysqlDSL) pluginRegistry.getDsl(MysqlDialectPlugin.DIALECT_NAME, "8.0.35").orElseThrow();
    //
    //      // Use DATE_FORMAT fluent API to format birthdates
    //      String sql = dsl.select()
    //              .column("name")
    //              .dateFormat("birthdate", "%Y-%m-%d")
    //              .as("formatted_date")
    //              .from("users")
    //              .orderBy("name")
    //              .fetch(5)
    //              .build();
    //
    //      // Verify SQL contains DATE_FORMAT
    //      assertThat(sql).containsIgnoringCase("DATE_FORMAT");
    //
    //      // Execute and verify results
    //      try (var ps = connection.prepareStatement(sql);
    //              var rs = ps.executeQuery()) {
    //          int count = 0;
    //          while (rs.next()) {
    //              count++;
    //              String name = rs.getString("name");
    //              String formattedDate = rs.getString("formatted_date");
    //
    //              assertThat(name).isNotNull();
    //              assertThat(formattedDate).isNotNull();
    //              // Verify format is YYYY-MM-DD
    //              assertThat(formattedDate).matches("\\d{4}-\\d{2}-\\d{2}");
    //          }
    //          assertThat(count).isEqualTo(5);
    //      }
    //  }
    //
    //  @Test
    //  void shouldExecuteComplexQueryWithMultipleMySQLFunctions() throws SQLException {
    //      // Get DSL from registry
    //      var dsl = (MysqlDSL) pluginRegistry.getDsl(MysqlDialectPlugin.DIALECT_NAME, "8.0.35").orElseThrow();
    //
    //      // Combine multiple MySQL custom functions: GROUP_CONCAT and COUNT
    //      String sql = dsl.select()
    //              .column("age")
    //              .groupConcat("name")
    //              .separator(" | ")
    //              .as("users_list")
    //              .countStar()
    //              .as("user_count")
    //              .from("users")
    //              .where()
    //              .column("active")
    //              .eq(true)
    //              .groupBy("age")
    //              .orderBy("age")
    //              .build();
    //
    //      // Verify SQL contains custom functions
    //      assertThat(sql).containsIgnoringCase("GROUP_CONCAT");
    //      assertThat(sql).containsIgnoringCase("COUNT");
    //
    //      // Execute and verify results
    //      try (var ps = connection.prepareStatement(sql);
    //              var rs = ps.executeQuery()) {
    //          boolean foundResults = false;
    //          while (rs.next()) {
    //              foundResults = true;
    //              int age = rs.getInt("age");
    //              String usersList = rs.getString("users_list");
    //              int userCount = rs.getInt("user_count");
    //
    //              assertThat(age).isGreaterThan(0);
    //              assertThat(usersList).isNotNull();
    //              assertThat(userCount).isGreaterThanOrEqualTo(1);
    //
    //              // Verify GROUP_CONCAT with custom separator
    //              if (userCount > 1) {
    //                  assertThat(usersList).contains(" | ");
    //              }
    //          }
    //          assertThat(foundResults).isTrue();
    //      }
    //  }
    //
    //  @Test
    //  void shouldExecuteConcatFunctionWithBuilder() throws SQLException {
    //      var dsl = (MysqlDSL) pluginRegistry.getDsl(MysqlDialectPlugin.DIALECT_NAME, "8.0.35").orElseThrow();
    //
    //      String sql = dsl.select()
    //              .concat()
    //              .column("name")
    //              .column("email")
    //              .as("name_email")
    //              .from("users")
    //              .where()
    //              .column("id")
    //              .eq(1)
    //              .build();
    //
    //      assertThat(sql).containsIgnoringCase("CONCAT");
    //
    //      try (var ps = connection.prepareStatement(sql);
    //              var rs = ps.executeQuery()) {
    //          assertThat(rs.next()).isTrue();
    //          String result = rs.getString("name_email");
    //          assertThat(result).isEqualTo("John Doejohn@example.com");
    //      }
    //  }
    //
    //  @Test
    //  void shouldExecuteCoalesceFunctionWithBuilder() throws SQLException {
    //      var dsl = (MysqlDSL) pluginRegistry.getDsl(MysqlDialectPlugin.DIALECT_NAME, "8.0.35").orElseThrow();
    //
    //      // COALESCE returns first non-NULL value
    //      String sql = dsl.select()
    //              .coalesce()
    //              .column("email")
    //              .column("name")
    //              .as("contact")
    //              .from("users")
    //              .where()
    //              .column("id")
    //              .eq(1)
    //              .build();
    //
    //      assertThat(sql).containsIgnoringCase("COALESCE");
    //
    //      try (var ps = connection.prepareStatement(sql);
    //              var rs = ps.executeQuery()) {
    //          assertThat(rs.next()).isTrue();
    //          String result = rs.getString("contact");
    //          assertThat(result).isEqualTo("john@example.com");
    //      }
    //  }
    //
    //  @Test
    //  void shouldExecuteIfnullFunction() throws SQLException {
    //      var dsl = (MysqlDSL) pluginRegistry.getDsl(MysqlDialectPlugin.DIALECT_NAME, "8.0.35").orElseThrow();
    //
    //      // Add a row with NULL email for testing
    //      try (var stmt = connection.createStatement()) {
    //          stmt.execute("INSERT INTO users VALUES (99, 'Test User', NULL, 30, true, '1990-01-01', '2023-01-01')");
    //      }
    //
    //      String sql = dsl.select()
    //              .column("name")
    //              .ifnull("email", "no-email@example.com")
    //              .as("contact_email")
    //              .from("users")
    //              .where()
    //              .column("id")
    //              .eq(99)
    //              .build();
    //
    //      assertThat(sql).containsIgnoringCase("IFNULL");
    //
    //      try (var ps = connection.prepareStatement(sql);
    //              var rs = ps.executeQuery()) {
    //          assertThat(rs.next()).isTrue();
    //          String name = rs.getString("name");
    //          String email = rs.getString("contact_email");
    //          assertThat(name).isEqualTo("Test User");
    //          assertThat(email).isEqualTo("no-email@example.com");
    //      }
    //
    //      // Clean up test data
    //      try (var stmt = connection.createStatement()) {
    //          stmt.execute("DELETE FROM users WHERE id = 99");
    //      }
    //  }
    //
    //  @Test
    //  void shouldExecuteInheritedSumAggregateFunction() throws SQLException {
    //      var dsl = (MysqlDSL) pluginRegistry.getDsl(MysqlDialectPlugin.DIALECT_NAME, "8.0.35").orElseThrow();
    //
    //      String sql = dsl.select().sum("age").as("total_age").from("users").build();
    //
    //      assertThat(sql).containsIgnoringCase("SUM");
    //
    //      try (var ps = connection.prepareStatement(sql);
    //              var rs = ps.executeQuery()) {
    //          assertThat(rs.next()).isTrue();
    //          int totalAge = rs.getInt("total_age");
    //          assertThat(totalAge).isEqualTo(293); // Sum of all ages: 30+25+15+35+30+25+40+35+28+30
    //      }
    //  }
    //
    //  @Test
    //  void shouldExecuteInheritedCountDistinctAggregateFunction() throws SQLException {
    //      var dsl = (MysqlDSL) pluginRegistry.getDsl(MysqlDialectPlugin.DIALECT_NAME, "8.0.35").orElseThrow();
    //
    //      String sql = dsl.select()
    //              .countDistinct("age")
    //              .as("distinct_ages")
    //              .from("users")
    //              .build();
    //
    //      assertThat(sql).containsIgnoringCase("COUNT");
    //      assertThat(sql).containsIgnoringCase("DISTINCT");
    //
    //      try (var ps = connection.prepareStatement(sql);
    //              var rs = ps.executeQuery()) {
    //          assertThat(rs.next()).isTrue();
    //          int distinctAges = rs.getInt("distinct_ages");
    //          assertThat(distinctAges).isEqualTo(6); // Distinct ages: 15, 25, 28, 30, 35, 40
    //      }
    //  }
    //
    //  @Test
    //  void shouldExecuteIfFunctionWithComplexCondition() throws SQLException {
    //      var dsl = (MysqlDSL) pluginRegistry.getDsl(MysqlDialectPlugin.DIALECT_NAME, "8.0.35").orElseThrow();
    //
    //      String sql = dsl.select()
    //              .column("name")
    //              .ifExpr()
    //              .when("age")
    //              .gte(30)
    //              .then("Adult")
    //              .otherwise("Young")
    //              .as("age_category")
    //              .from("users")
    //              .orderBy("id")
    //              .build();
    //
    //      assertThat(sql).containsIgnoringCase("IF");
    //
    //      try (var ps = connection.prepareStatement(sql);
    //              var rs = ps.executeQuery()) {
    //          int count = 0;
    //          while (rs.next()) {
    //              count++;
    //              String name = rs.getString("name");
    //              String category = rs.getString("age_category");
    //
    //              assertThat(name).isNotNull();
    //              assertThat(category).isIn("Adult", "Young");
    //
    //              // Verify specific cases
    //              if (name.equals("John Doe")) {
    //                  assertThat(category).isEqualTo("Adult"); // age = 30
    //              } else if (name.equals("Bob")) {
    //                  assertThat(category).isEqualTo("Young"); // age = 15
    //              }
    //          }
    //          assertThat(count).isEqualTo(10);
    //      }
    //  }
}
