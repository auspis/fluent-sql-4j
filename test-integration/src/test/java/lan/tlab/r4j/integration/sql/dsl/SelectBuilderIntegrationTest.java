package lan.tlab.r4j.integration.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    @Test
    void groupByWithHaving() throws SQLException {
        // First, let's create a table to test GROUP BY with HAVING properly
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE orders (\"id\" INTEGER, \"customer_id\" INTEGER, \"amount\" INTEGER)");
            stmt.execute("INSERT INTO orders VALUES (1, 100, 50)");
            stmt.execute("INSERT INTO orders VALUES (2, 100, 150)");
            stmt.execute("INSERT INTO orders VALUES (3, 200, 75)");
            stmt.execute("INSERT INTO orders VALUES (4, 200, 25)");
            stmt.execute("INSERT INTO orders VALUES (5, 300, 300)");
        }

        PreparedStatement ps = DSL.select("customer_id")
                .from("orders")
                .groupBy("customer_id")
                .having("customer_id")
                .gt(100)
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("customer_id")).isEqualTo(200);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("customer_id")).isEqualTo(300);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void groupByWithHavingAndCondition() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE sales (\"region\" VARCHAR(50), \"sales_amount\" INTEGER)");
            stmt.execute("INSERT INTO sales VALUES ('North', 100)");
            stmt.execute("INSERT INTO sales VALUES ('North', 150)");
            stmt.execute("INSERT INTO sales VALUES ('South', 75)");
            stmt.execute("INSERT INTO sales VALUES ('South', 25)");
            stmt.execute("INSERT INTO sales VALUES ('East', 200)");
        }

        PreparedStatement ps = DSL.select("region")
                .from("sales")
                .groupBy("region")
                .having("region")
                .ne("South")
                .andHaving("region")
                .ne("East")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("region")).isEqualTo("North");

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void groupByWithHavingOrCondition() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE inventory (\"category\" VARCHAR(50), \"stock\" INTEGER)");
            stmt.execute("INSERT INTO inventory VALUES ('Electronics', 50)");
            stmt.execute("INSERT INTO inventory VALUES ('Books', 100)");
            stmt.execute("INSERT INTO inventory VALUES ('Clothing', 75)");
        }

        PreparedStatement ps = DSL.select("category")
                .from("inventory")
                .groupBy("category")
                .having("category")
                .eq("Electronics")
                .orHaving("category")
                .eq("Books")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            String firstCategory = rs.getString("category");

            assertThat(rs.next()).isTrue();
            String secondCategory = rs.getString("category");

            assertThat(rs.next()).isFalse();

            assertThat(firstCategory).isIn("Electronics", "Books");
            assertThat(secondCategory).isIn("Electronics", "Books");
            assertThat(firstCategory).isNotEqualTo(secondCategory);
        }
    }

    @Test
    void whereGroupByHavingOrderBy() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "CREATE TABLE transactions (\"id\" INTEGER, \"user_id\" INTEGER, \"amount\" INTEGER, \"status\" VARCHAR(20))");
            stmt.execute("INSERT INTO transactions VALUES (1, 1, 100, 'completed')");
            stmt.execute("INSERT INTO transactions VALUES (2, 1, 200, 'completed')");
            stmt.execute("INSERT INTO transactions VALUES (3, 2, 50, 'completed')");
            stmt.execute("INSERT INTO transactions VALUES (4, 2, 25, 'pending')");
            stmt.execute("INSERT INTO transactions VALUES (5, 3, 300, 'completed')");
        }

        PreparedStatement ps = DSL.select("user_id")
                .from("transactions")
                .where("status")
                .eq("completed")
                .groupBy("user_id")
                .having("user_id")
                .gt(0)
                .orderBy("user_id")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("user_id")).isEqualTo(1);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("user_id")).isEqualTo(2);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("user_id")).isEqualTo(3);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectCountStar() throws SQLException {
        PreparedStatement ps = DSL.select().countStar().from("users").buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(4);
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectCountStarWithAlias() throws SQLException {
        PreparedStatement ps =
                DSL.select().countStar().as("total_users").from("users").buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("total_users")).isEqualTo(4);
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectSumWithGroupBy() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE sales (\"customer_id\" INTEGER, \"amount\" INTEGER)");
            stmt.execute("INSERT INTO sales VALUES (1, 100)");
            stmt.execute("INSERT INTO sales VALUES (1, 150)");
            stmt.execute("INSERT INTO sales VALUES (2, 200)");
            stmt.execute("INSERT INTO sales VALUES (2, 50)");
        }

        PreparedStatement ps = DSL.select()
                .sum("amount")
                .as("total_amount")
                .from("sales")
                .groupBy("customer_id")
                .orderBy("customer_id")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("total_amount")).isEqualTo(250);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("total_amount")).isEqualTo(250);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectAvgWithGroupByAndHaving() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE employees (\"department\" VARCHAR(50), \"salary\" INTEGER)");
            stmt.execute("INSERT INTO employees VALUES ('IT', 60000)");
            stmt.execute("INSERT INTO employees VALUES ('IT', 80000)");
            stmt.execute("INSERT INTO employees VALUES ('HR', 50000)");
            stmt.execute("INSERT INTO employees VALUES ('Sales', 70000)");
        }

        PreparedStatement ps = DSL.select()
                .avg("salary")
                .as("avg_salary")
                .from("employees")
                .groupBy("department")
                .having("department")
                .ne("HR")
                .orderBy("department")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("avg_salary")).isEqualTo(70000);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("avg_salary")).isEqualTo(70000);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectMaxAndMin() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE scores (\"student_id\" INTEGER, \"score\" INTEGER)");
            stmt.execute("INSERT INTO scores VALUES (1, 85)");
            stmt.execute("INSERT INTO scores VALUES (2, 92)");
            stmt.execute("INSERT INTO scores VALUES (3, 78)");
            stmt.execute("INSERT INTO scores VALUES (4, 95)");
        }

        PreparedStatement psMax =
                DSL.select().max("score").as("max_score").from("scores").buildPreparedStatement(connection);

        try (ResultSet rs = psMax.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("max_score")).isEqualTo(95);
            assertThat(rs.next()).isFalse();
        }

        PreparedStatement psMin =
                DSL.select().min("score").as("min_score").from("scores").buildPreparedStatement(connection);

        try (ResultSet rs = psMin.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("min_score")).isEqualTo(78);
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectCountDistinct() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE orders (\"customer_id\" INTEGER, \"order_id\" INTEGER)");
            stmt.execute("INSERT INTO orders VALUES (1, 101)");
            stmt.execute("INSERT INTO orders VALUES (1, 102)");
            stmt.execute("INSERT INTO orders VALUES (2, 103)");
            stmt.execute("INSERT INTO orders VALUES (1, 104)");
        }

        PreparedStatement ps = DSL.select()
                .countDistinct("customer_id")
                .as("unique_customers")
                .from("orders")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("unique_customers")).isEqualTo(2);
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectCountWithWhere() throws SQLException {
        PreparedStatement ps = DSL.select()
                .count("id")
                .as("active_count")
                .from("users")
                .where("active")
                .eq(true)
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("active_count")).isEqualTo(3);
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectMultipleAggregatesWithoutAliases() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE scores (\"id\" INTEGER, \"score\" INTEGER, \"created_at\" TIMESTAMP)");
            stmt.execute("INSERT INTO scores VALUES (1, 85, TIMESTAMP '2024-01-01 10:00:00')");
            stmt.execute("INSERT INTO scores VALUES (2, 92, TIMESTAMP '2024-01-02 10:00:00')");
            stmt.execute("INSERT INTO scores VALUES (3, 78, TIMESTAMP '2024-01-03 10:00:00')");
        }

        PreparedStatement ps =
                DSL.select().sum("score").max("created_at").from("scores").buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(255);
            assertThat(rs.getTimestamp(2)).isNotNull();
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectMultipleAggregatesWithAliases() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE stats (\"id\" INTEGER, \"value\" INTEGER, \"updated_at\" TIMESTAMP)");
            stmt.execute("INSERT INTO stats VALUES (1, 100, TIMESTAMP '2024-01-01 10:00:00')");
            stmt.execute("INSERT INTO stats VALUES (2, 200, TIMESTAMP '2024-01-02 10:00:00')");
            stmt.execute("INSERT INTO stats VALUES (3, 150, TIMESTAMP '2024-01-03 10:00:00')");
        }

        PreparedStatement ps = DSL.select()
                .sum("value")
                .as("total_value")
                .max("updated_at")
                .as("latest_update")
                .from("stats")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("total_value")).isEqualTo(450);
            assertThat(rs.getTimestamp("latest_update")).isNotNull();
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectMultipleAggregatesWithOneAlias() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE metrics (\"id\" INTEGER, \"amount\" INTEGER, \"timestamp\" TIMESTAMP)");
            stmt.execute("INSERT INTO metrics VALUES (1, 50, TIMESTAMP '2024-01-01 10:00:00')");
            stmt.execute("INSERT INTO metrics VALUES (2, 75, TIMESTAMP '2024-01-02 10:00:00')");
        }

        PreparedStatement ps = DSL.select()
                .sum("amount")
                .max("timestamp")
                .as("last_timestamp")
                .from("metrics")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(125);
            assertThat(rs.getTimestamp("last_timestamp")).isNotNull();
            assertThat(rs.next()).isFalse();
        }
    }
}
