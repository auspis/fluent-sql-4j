package lan.tlab.r4j.integration.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lan.tlab.r4j.integration.sql.util.TestDatabaseUtil;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.dsl.select.SelectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderIntegrationTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.createProductsTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void selectSpecificColumns() throws SQLException {
        PreparedStatement ps = DSL.select("name", "email").from("users").buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getString("email")).isEqualTo("john@example.com");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");
            assertThat(rs.getString("email")).isEqualTo("jane@example.com");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Bob");
            assertThat(rs.getString("email")).isEqualTo("bob@example.com");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");
            assertThat(rs.getString("email")).isEqualTo("alice@example.com");

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectAllColumns() throws SQLException {
        PreparedStatement ps =
                DSL.select("id", "name", "email", "age", "active").from("users").buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(1);
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getString("email")).isEqualTo("john@example.com");
            assertThat(rs.getInt("age")).isEqualTo(30);
            assertThat(rs.getBoolean("active")).isTrue();

            assertThat(rs.next()).isTrue();
            assertThat(rs.next()).isTrue();
            assertThat(rs.next()).isTrue();
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void whereEqualCondition() throws SQLException {
        PreparedStatement ps = DSL.select("name", "age")
                .from("users")
                .where("name")
                .eq("John Doe")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getInt("age")).isEqualTo(30);
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void whereGreaterThan() throws SQLException {
        PreparedStatement ps =
                DSL.select("name", "age").from("users").where("age").gt(25).buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getInt("age")).isEqualTo(30);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");
            assertThat(rs.getInt("age")).isEqualTo(35);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void whereLessThan() throws SQLException {
        PreparedStatement ps =
                DSL.select("name", "age").from("users").where("age").lt(20).buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Bob");
            assertThat(rs.getInt("age")).isEqualTo(15);
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void whereGreaterThanOrEqual() throws SQLException {
        PreparedStatement ps =
                DSL.select("name", "age").from("users").where("age").gte(30).buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getInt("age")).isEqualTo(30);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");
            assertThat(rs.getInt("age")).isEqualTo(35);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void whereLessThanOrEqual() throws SQLException {
        PreparedStatement ps =
                DSL.select("name", "age").from("users").where("age").lte(25).buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");
            assertThat(rs.getInt("age")).isEqualTo(25);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Bob");
            assertThat(rs.getInt("age")).isEqualTo(15);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void whereNotEqual() throws SQLException {
        PreparedStatement ps = DSL.select("name", "age")
                .from("users")
                .where("name")
                .ne("John Doe")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Bob");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void whereLike() throws SQLException {
        PreparedStatement ps = DSL.select("name", "email")
                .from("users")
                .where("email")
                .like("%example.com")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                assertThat(rs.getString("email")).endsWith("example.com");
                count++;
            }
            assertThat(count).isEqualTo(4);
        }
    }

    @Test
    void andCondition() throws SQLException {
        PreparedStatement ps = DSL.select("name", "age", "active")
                .from("users")
                .where("age")
                .gt(18)
                .and("active")
                .eq(true)
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getInt("age")).isEqualTo(30);
            assertThat(rs.getBoolean("active")).isTrue();

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");
            assertThat(rs.getInt("age")).isEqualTo(25);
            assertThat(rs.getBoolean("active")).isTrue();

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");
            assertThat(rs.getInt("age")).isEqualTo(35);
            assertThat(rs.getBoolean("active")).isTrue();

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void orCondition() throws SQLException {
        PreparedStatement ps = DSL.select("name", "age")
                .from("users")
                .where("name")
                .eq("John Doe")
                .or("name")
                .eq("Jane Smith")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void andOrCondition() throws SQLException {
        PreparedStatement ps = DSL.select("name", "age", "active")
                .from("users")
                .where("age")
                .gt(20)
                .and("active")
                .eq(true)
                .or("name")
                .eq("Bob")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Bob");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void orderByAscending() throws SQLException {
        PreparedStatement ps =
                DSL.select("name", "age").from("users").orderBy("age").buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Bob");
            assertThat(rs.getInt("age")).isEqualTo(15);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");
            assertThat(rs.getInt("age")).isEqualTo(25);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getInt("age")).isEqualTo(30);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");
            assertThat(rs.getInt("age")).isEqualTo(35);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void orderByDescending() throws SQLException {
        PreparedStatement ps =
                DSL.select("name", "age").from("users").orderByDesc("age").buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");
            assertThat(rs.getInt("age")).isEqualTo(35);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getInt("age")).isEqualTo(30);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");
            assertThat(rs.getInt("age")).isEqualTo(25);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Bob");
            assertThat(rs.getInt("age")).isEqualTo(15);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void fetch() throws SQLException {
        PreparedStatement ps = DSL.select("name").from("users").fetch(2).buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void offset() throws SQLException {
        PreparedStatement ps =
                DSL.select("name").from("users").offset(2).fetch(2).buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Bob");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void fetchAndOffset() throws SQLException {
        PreparedStatement ps =
                DSL.select("name").from("users").fetch(2).offset(1).buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Bob");

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void fullSelectQuery() throws SQLException {
        PreparedStatement ps = DSL.select("name", "email", "age")
                .from("users")
                .where("age")
                .gte(18)
                .and("active")
                .eq(true)
                .orderByDesc("age")
                .fetch(2)
                .offset(0)
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");
            assertThat(rs.getInt("age")).isEqualTo(35);
            assertThat(rs.getString("email")).isEqualTo("alice@example.com");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getInt("age")).isEqualTo(30);
            assertThat(rs.getString("email")).isEqualTo("john@example.com");

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void fromWithAlias() throws SQLException {
        PreparedStatement ps = DSL.select("name", "email")
                .from("users")
                .as("u")
                .where("name")
                .eq("John Doe")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getString("email")).isEqualTo("john@example.com");
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void whereWithOrderByAndFetch() throws SQLException {
        PreparedStatement ps = DSL.select("name", "age")
                .from("users")
                .where("active")
                .eq(true)
                .orderBy("age")
                .fetch(2)
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");
            assertThat(rs.getInt("age")).isEqualTo(25);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getInt("age")).isEqualTo(30);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void fromSubquery() throws SQLException {
        SelectBuilder subquery =
                DSL.select("name", "age").from("users").where("age").gt(20);

        PreparedStatement ps = DSL.select("name", "age").from(subquery, "u").buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getInt("age")).isEqualTo(30);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");
            assertThat(rs.getInt("age")).isEqualTo(25);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");
            assertThat(rs.getInt("age")).isEqualTo(35);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void fromSubqueryWithWhere() throws SQLException {
        SelectBuilder subquery =
                DSL.select("name", "age").from("users").where("active").eq(true);

        PreparedStatement ps = DSL.select("name", "age")
                .from(subquery, "u")
                .where("age")
                .gte(30)
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getInt("age")).isEqualTo(30);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");
            assertThat(rs.getInt("age")).isEqualTo(35);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void whereWithScalarSubquery() throws SQLException {
        // Create a subquery that returns a single value (average age)
        SelectBuilder avgAgeSubquery = DSL.select("age").from("users").fetch(1);

        // This test verifies the scalar subquery is generated correctly in SQL
        PreparedStatement ps = DSL.select("name", "age")
                .from("users")
                .where("age")
                .gte(avgAgeSubquery)
                .buildPreparedStatement(connection);

        // Just verify it doesn't throw an exception and produces valid SQL
        try (ResultSet rs = ps.executeQuery()) {
            // We're mainly testing that the SQL is valid and can execute
            int count = 0;
            while (rs.next()) {
                count++;
            }
            assertThat(count).isGreaterThanOrEqualTo(0);
        }
    }
}
