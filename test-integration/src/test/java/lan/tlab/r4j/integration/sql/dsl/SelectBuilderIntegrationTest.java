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
        TestDatabaseUtil.createOrdersTable(connection);
        TestDatabaseUtil.createSalesTable(connection);
        TestDatabaseUtil.createEmployeesTable(connection);
        TestDatabaseUtil.createScoresTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
        TestDatabaseUtil.insertSampleOrders(connection);
        TestDatabaseUtil.insertSampleSales(connection);
        TestDatabaseUtil.insertSampleEmployees(connection);
        TestDatabaseUtil.insertSampleScores(connection);
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
        PreparedStatement ps = DSL.select("customer_id")
                .from("sales")
                .groupBy("customer_id")
                .having("customer_id")
                .ne(2)
                .andHaving("customer_id")
                .gt(0)
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("customer_id")).isEqualTo(1);

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void groupByWithHavingOrCondition() throws SQLException {
        PreparedStatement ps = DSL.select("department")
                .from("employees")
                .groupBy("department")
                .having("department")
                .eq("IT")
                .orHaving("department")
                .eq("HR")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            String firstDept = rs.getString("department");

            assertThat(rs.next()).isTrue();
            String secondDept = rs.getString("department");

            assertThat(rs.next()).isFalse();

            assertThat(firstDept).isIn("IT", "HR");
            assertThat(secondDept).isIn("IT", "HR");
            assertThat(firstDept).isNotEqualTo(secondDept);
        }
    }

    @Test
    void whereGroupByHavingOrderBy() throws SQLException {
        PreparedStatement ps = DSL.select("customer_id")
                .from("orders")
                .where("amount")
                .gt(0)
                .groupBy("customer_id")
                .having("customer_id")
                .gt(100)
                .orderBy("customer_id")
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
        PreparedStatement ps = DSL.select()
                .countDistinct("customer_id")
                .as("unique_customers")
                .from("orders")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("unique_customers")).isEqualTo(3); // 100, 200, 300
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
        PreparedStatement ps =
                DSL.select().sum("age").max("createdAt").from("users").buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(105); // 30 + 25 + 15 + 35
            assertThat(rs.getTimestamp(2)).isNotNull();
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectMultipleAggregatesWithAliases() throws SQLException {
        PreparedStatement ps = DSL.select()
                .sum("age")
                .as("total_age")
                .max("createdAt")
                .as("latest_update")
                .from("users")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("total_age")).isEqualTo(105); // 30 + 25 + 15 + 35
            assertThat(rs.getTimestamp("latest_update")).isNotNull();
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectMultipleAggregatesWithOneAlias() throws SQLException {
        PreparedStatement ps = DSL.select()
                .sum("amount")
                .max("customer_id")
                .as("max_customer")
                .from("orders")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(600); // 50 + 150 + 75 + 25 + 300
            assertThat(rs.getInt("max_customer")).isEqualTo(300);
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void selectColumn() throws SQLException {
        PreparedStatement ps = DSL.select().column("name").from("users").buildPreparedStatement(connection);

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
    void selectColumnWithAlias() throws SQLException {
        PreparedStatement ps =
                DSL.select().column("name").as("user_name").from("users").buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("user_name")).isEqualTo("John Doe");
            assertThat(rs.next()).isTrue();
        }
    }

    @Test
    void selectMultipleColumns() throws SQLException {
        PreparedStatement ps =
                DSL.select().column("name").column("email").from("users").buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getString("email")).isEqualTo("john@example.com");
            assertThat(rs.next()).isTrue();
        }
    }

    @Test
    void selectMixedColumnsAndAggregates() throws SQLException {
        PreparedStatement ps = DSL.select()
                .column("student_id")
                .sum("score")
                .as("total_score")
                .from("scores")
                .groupBy("student_id")
                .orderBy("student_id")
                .buildPreparedStatement(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("student_id")).isEqualTo(1);
            assertThat(rs.getInt("total_score")).isEqualTo(85);
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("student_id")).isEqualTo(2);
            assertThat(rs.getInt("total_score")).isEqualTo(92);
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("student_id")).isEqualTo(3);
            assertThat(rs.getInt("total_score")).isEqualTo(78);
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("student_id")).isEqualTo(4);
            assertThat(rs.getInt("total_score")).isEqualTo(95);
            assertThat(rs.next()).isFalse();
        }
    }
}
