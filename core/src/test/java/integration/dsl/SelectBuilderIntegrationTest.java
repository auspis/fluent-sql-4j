package integration.dsl;

import static io.github.auspis.fluentsql4j.test.JsonAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.dsl.select.SelectBuilder;
import io.github.auspis.fluentsql4j.dsl.util.ResultSetUtil;
import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.auspis.fluentsql4j.test.util.TestDatabaseUtil;
import io.github.auspis.fluentsql4j.test.util.annotation.IntegrationTest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for SelectBuilder with H2 in-memory database.
 * Tests the complete integration between DSL SelectBuilder, SQL rendering,
 * PreparedStatement creation, and actual database query execution.
 */
@IntegrationTest
class SelectBuilderIntegrationTest {

    private Connection connection;
    private DSL dsl;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        dsl = StandardSqlUtil.dsl();
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
        try (PreparedStatement ps = dsl.select("name", "email").from("users").build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getString("email")));

            assertThat(rows)
                    .hasSize(10)
                    .extracting(r -> (String) r.get(0), r -> (String) r.get(1))
                    .contains(
                            tuple("John Doe", "john@example.com"),
                            tuple("Jane Smith", "jane@example.com"),
                            tuple("Bob", "bob@example.com"),
                            tuple("Alice", "alice@example.com"));
        }
    }

    @Test
    void selectAllColumns() throws SQLException {
        try (PreparedStatement ps =
                dsl.select("id", "name", "email", "age", "active").from("users").build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(
                    ps,
                    r -> List.of(
                            r.getInt("id"),
                            r.getString("name"),
                            r.getString("email"),
                            r.getInt("age"),
                            r.getBoolean("active")));

            assertThat(rows)
                    .hasSize(10)
                    .extracting(
                            r -> (Integer) r.get(0),
                            r -> (String) r.get(1),
                            r -> (String) r.get(2),
                            r -> (Integer) r.get(3),
                            r -> (Boolean) r.get(4))
                    .contains(tuple(1, "John Doe", "john@example.com", 30, true));
        }
    }

    @Test
    void whereEqualCondition() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "age")
                .from("users")
                .where()
                .column("name")
                .eq("John Doe")
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age")));

            assertThat(rows)
                    .hasSize(1)
                    .extracting(r -> (String) r.get(0), r -> (Integer) r.get(1))
                    .containsExactly(tuple("John Doe", 30));
        }
    }

    @Test
    void whereGreaterThan() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "age")
                .from("users")
                .where()
                .column("age")
                .gt(25)
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age")));

            assertThat(rows)
                    .hasSize(7)
                    .extracting(r -> (String) r.get(0), r -> (Integer) r.get(1))
                    .contains(tuple("John Doe", 30), tuple("Alice", 35));
        }
    }

    @Test
    void whereLessThan() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "age")
                .from("users")
                .where()
                .column("age")
                .lt(20)
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age")));

            assertThat(rows)
                    .hasSize(1)
                    .extracting(r -> (String) r.get(0), r -> (Integer) r.get(1))
                    .containsExactly(tuple("Bob", 15));
        }
    }

    @Test
    void whereGreaterThanOrEqual() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "age")
                .from("users")
                .where()
                .column("age")
                .gte(30)
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age")));

            assertThat(rows)
                    .hasSize(6)
                    .extracting(r -> (String) r.get(0), r -> (Integer) r.get(1))
                    .contains(tuple("John Doe", 30), tuple("Alice", 35));
        }
    }

    @Test
    void whereLessThanOrEqual() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "age")
                .from("users")
                .where()
                .column("age")
                .lte(25)
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age")));

            assertThat(rows)
                    .hasSize(3)
                    .extracting(r -> (String) r.get(0), r -> (Integer) r.get(1))
                    .contains(tuple("Jane Smith", 25), tuple("Bob", 15), tuple("Diana", 25));
        }
    }

    @Test
    void whereNotEqual() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "age")
                .from("users")
                .where()
                .column("name")
                .ne("John Doe")
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age")));

            assertThat(rows).hasSize(9).extracting(r -> (String) r.get(0)).contains("Jane Smith", "Bob", "Alice");
        }
    }

    @Test
    void whereLike() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "email")
                .from("users")
                .where()
                .column("email")
                .like("%example.com")
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getString("email")));

            assertThat(rows).hasSize(10);
            assertThat(rows).extracting(r -> (String) r.get(1)).allMatch(email -> email.endsWith("example.com"));
        }
    }

    @Test
    void andCondition() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "age", "active")
                .from("users")
                .where()
                .column("age")
                .gt(18)
                .and()
                .column("active")
                .eq(true)
                .build(connection)) {

            List<List<Object>> results =
                    ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age"), r.getBoolean("active")));

            assertThat(results)
                    .hasSize(7)
                    .extracting(r -> (String) r.get(0), r -> (Integer) r.get(1), r -> (Boolean) r.get(2))
                    .contains(tuple("John Doe", 30, true), tuple("Jane Smith", 25, true), tuple("Alice", 35, true));
        }
    }

    @Test
    void orCondition() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "age")
                .from("users")
                .where()
                .column("name")
                .eq("John Doe")
                .or()
                .column("name")
                .eq("Jane Smith")
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age")));

            assertThat(rows)
                    .hasSize(2)
                    .extracting(r -> (String) r.get(0))
                    .containsExactlyInAnyOrder("John Doe", "Jane Smith");
        }
    }

    @Test
    void andOrCondition() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "age", "active")
                .from("users")
                .where()
                .column("age")
                .gt(20)
                .and()
                .column("active")
                .eq(true)
                .or()
                .column("name")
                .eq("Bob")
                .build(connection)) {
            List<List<Object>> rows =
                    ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age"), r.getBoolean("active")));

            assertThat(rows)
                    .isNotEmpty()
                    .extracting(r -> (String) r.get(0))
                    .contains("John Doe", "Jane Smith", "Bob", "Alice");
        }
    }

    @Test
    void orderByAscending() throws SQLException {
        PreparedStatement ps =
                dsl.select("name", "age").from("users").orderBy().asc("age").build(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Bob");
            assertThat(rs.getInt("age")).isEqualTo(15);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isIn("Jane Smith", "Diana");
            assertThat(rs.getInt("age")).isEqualTo(25);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isIn("Jane Smith", "Diana");
            assertThat(rs.getInt("age")).isEqualTo(25);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Grace");
            assertThat(rs.getInt("age")).isEqualTo(28);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isIn("John Doe", "Charlie", "Henry");
            assertThat(rs.getInt("age")).isEqualTo(30);

            // Continue checking remaining users are in ascending order
            int previousAge = 30;
            while (rs.next()) {
                int currentAge = rs.getInt("age");
                assertThat(currentAge).isGreaterThanOrEqualTo(previousAge);
                previousAge = currentAge;
            }
        }
    }

    @Test
    void orderByDescending() throws SQLException {
        PreparedStatement ps =
                dsl.select("name", "age").from("users").orderBy().desc("age").build(connection);

        try (ResultSet rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Eve");
            assertThat(rs.getInt("age")).isEqualTo(40);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isIn("Alice", "Frank");
            assertThat(rs.getInt("age")).isEqualTo(35);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isIn("Alice", "Frank");
            assertThat(rs.getInt("age")).isEqualTo(35);

            // Continue checking remaining users are in descending order
            int previousAge = 35;
            while (rs.next()) {
                int currentAge = rs.getInt("age");
                assertThat(currentAge).isLessThanOrEqualTo(previousAge);
                previousAge = currentAge;
            }
        }
    }

    @Test
    void fetch() throws SQLException {
        try (PreparedStatement ps = dsl.select("name").from("users").fetch(2).build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name")));

            assertThat(rows).hasSize(2).extracting(r -> (String) r.get(0)).containsExactly("John Doe", "Jane Smith");
        }
    }

    @Test
    void offset() throws SQLException {
        try (PreparedStatement ps =
                dsl.select("name").from("users").offset(2).fetch(2).build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name")));

            assertThat(rows).hasSize(2).extracting(r -> (String) r.get(0)).containsExactly("Bob", "Alice");
        }
    }

    @Test
    void fetchAndOffset() throws SQLException {
        try (PreparedStatement ps =
                dsl.select("name").from("users").fetch(2).offset(1).build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name")));

            assertThat(rows).hasSize(2).extracting(r -> (String) r.get(0)).containsExactly("Jane Smith", "Bob");
        }
    }

    @Test
    void fullSelectQuery() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "email", "age")
                .from("users")
                .where()
                .column("age")
                .gte(18)
                .and()
                .column("active")
                .eq(true)
                .orderBy()
                .desc("age")
                .fetch(2)
                .offset(0)
                .build(connection)) {
            List<List<Object>> rows =
                    ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getString("email"), r.getInt("age")));

            assertThat(rows).hasSize(2);
            assertThat(rows.get(0)).extracting(r -> r).containsExactly("Eve", "eve@example.com", 40);
            assertThat(rows.get(1).get(0)).isIn("Alice", "Frank");
            assertThat(rows.get(1).get(2)).isEqualTo(35);
        }
    }

    @Test
    void fromWithAlias() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "email")
                .from("users")
                .as("u")
                .where()
                .column("name")
                .eq("John Doe")
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getString("email")));

            assertThat(rows)
                    .hasSize(1)
                    .extracting(r -> (String) r.get(0), r -> (String) r.get(1))
                    .containsExactly(tuple("John Doe", "john@example.com"));
        }
    }

    @Test
    void whereWithOrderByAndFetch() throws SQLException {
        try (PreparedStatement ps = dsl.select("name", "age")
                .from("users")
                .where()
                .column("active")
                .eq(true)
                .orderBy()
                .asc("age")
                .fetch(2)
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age")));

            assertThat(rows).hasSize(2);
            // First row should have age 25
            assertThat(rows.get(0).get(0)).isIn("Jane Smith", "Diana");
            assertThat(rows.get(0).get(1)).isEqualTo(25);
            // Second row could be Jane/Diana or Grace (28) or John/Charlie/Henry (30)
            assertThat(rows.get(1).get(1)).isIn(25, 28, 30);
        }
    }

    @Test
    void fromSubquery() throws SQLException {
        SelectBuilder subquery =
                dsl.select("name", "age").from("users").where().column("age").gt(20);

        try (PreparedStatement ps =
                dsl.select("name", "age").from(subquery, "u").build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age")));

            // Users with age > 20: Jane(25), Diana(25), Grace(28), John(30), Charlie(30), Henry(30), Alice(35),
            // Frank(35), Eve(40) = 9 users
            assertThat(rows).hasSize(9).extracting(r -> (Integer) r.get(1)).allMatch(age -> age > 20);
        }
    }

    @Test
    void fromSubqueryWithWhere() throws SQLException {
        SelectBuilder subquery =
                dsl.select("name", "age").from("users").where().column("active").eq(true);

        try (PreparedStatement ps = dsl.select("name", "age")
                .from(subquery, "u")
                .where()
                .column("age")
                .gte(30)
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age")));

            // Active users with age >= 30: John(30), Charlie(30), Henry(30), Alice(35), Frank(35), Eve(40) = 6 users
            assertThat(rows).hasSize(6).extracting(r -> (Integer) r.get(1)).allMatch(age -> age >= 30);
        }
    }

    @Test
    void whereWithScalarSubquery() throws SQLException {
        // Create a subquery that returns a single value (average age)
        SelectBuilder avgAgeSubquery = dsl.select("age").from("users").fetch(1);

        // This test verifies the scalar subquery is generated correctly in SQL
        try (PreparedStatement ps = dsl.select("name", "age")
                .from("users")
                .where()
                .column("age")
                .gte(avgAgeSubquery)
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getInt("age")));

            // We're mainly testing that the SQL is valid and can execute
            assertThat(rows).isNotNull();
        }
    }

    @Test
    void groupByWithHaving() throws SQLException {
        try (PreparedStatement ps = dsl.select("age")
                .from("users")
                .groupBy()
                .column("age")
                .having()
                .column("age")
                .gt(25)
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getInt("age")));

            // Verify we got distinct age groups: 28, 30, 35, 40
            assertThat(rows)
                    .hasSize(4)
                    .extracting(r -> (Integer) r.get(0))
                    .allMatch(age -> age > 25)
                    .containsExactlyInAnyOrder(28, 30, 35, 40);
        }
    }

    @Test
    void groupByWithHavingAndCondition() throws SQLException {
        try (PreparedStatement ps = dsl.select("age")
                .from("users")
                .groupBy()
                .column("age")
                .having()
                .column("age")
                .ne(25)
                .andHaving()
                .column("age")
                .gt(20)
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getInt("age")));

            // Verify we got age groups: 28, 30, 35, 40 (excluding 25, 15)
            assertThat(rows)
                    .hasSize(4)
                    .extracting(r -> (Integer) r.get(0))
                    .allMatch(age -> age != 25 && age > 20)
                    .containsExactlyInAnyOrder(28, 30, 35, 40);
        }
    }

    @Test
    void groupByWithHavingOrCondition() throws SQLException {
        try (PreparedStatement ps = dsl.select("age")
                .from("users")
                .groupBy()
                .column("age")
                .having()
                .column("age")
                .eq(25)
                .orHaving()
                .column("age")
                .eq(30)
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getInt("age")));

            // Should return two age groups: 25 and 30
            assertThat(rows).hasSize(2).extracting(r -> (Integer) r.get(0)).containsExactlyInAnyOrder(25, 30);
        }
    }

    @Test
    void whereGroupByHavingOrderBy() throws SQLException {
        try (PreparedStatement ps = dsl.select("age")
                .from("users")
                .where()
                .column("active")
                .eq(true)
                .groupBy()
                .column("age")
                .having()
                .column("age")
                .gte(30)
                .orderBy()
                .asc("age")
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getInt("age")));

            assertThat(rows).hasSize(3).extracting(r -> (Integer) r.get(0)).containsExactly(30, 35, 40);
        }
    }

    @Test
    void selectCountStar() throws SQLException {
        try (PreparedStatement ps = dsl.select().countStar().from("users").build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getInt(1)));

            assertThat(rows).hasSize(1).extracting(r -> (Integer) r.get(0)).containsExactly(10);
        }
    }

    @Test
    void selectCountStarWithAlias() throws SQLException {
        try (PreparedStatement ps =
                dsl.select().countStar().as("total_users").from("users").build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getInt("total_users")));

            assertThat(rows).hasSize(1).extracting(r -> (Integer) r.get(0)).containsExactly(10);
        }
    }

    @Test
    void selectSumWithGroupBy() throws SQLException {
        try (PreparedStatement ps = dsl.select()
                .sum("id")
                .as("total_ids")
                .from("users")
                .groupBy()
                .column("age")
                .orderBy()
                .asc("age")
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getInt("total_ids")));

            assertThat(rows)
                    .hasSize(6)
                    .extracting(r -> (Integer) r.get(0))
                    .containsExactly(
                            3, // age 15: user id 3
                            8, // age 25: user ids 2, 6
                            9, // age 28: user id 9
                            16, // age 30: user ids 1, 5, 10
                            12, // age 35: user ids 4, 8
                            7 // age 40: user id 7
                            );
        }
    }

    @Test
    void selectAvgWithGroupByAndHaving() throws SQLException {
        try (PreparedStatement ps = dsl.select()
                .avg("id")
                .as("avg_id")
                .from("users")
                .groupBy()
                .column("age")
                .having()
                .column("age")
                .gte(30)
                .orderBy()
                .asc("age")
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getDouble("avg_id")));

            assertThat(rows).hasSize(3);
            assertThat((Double) rows.get(0).get(0))
                    .isCloseTo(5.33, org.assertj.core.data.Offset.offset(0.1)); // age 30: AVG(1,5,10)
            assertThat((Double) rows.get(1).get(0)).isEqualTo(6.0); // age 35: AVG(4,8)
            assertThat((Double) rows.get(2).get(0)).isEqualTo(7.0); // age 40: AVG(7)
        }
    }

    @Test
    void selectMaxAndMin() throws SQLException {
        try (PreparedStatement psMax =
                dsl.select().max("age").as("max_age").from("users").build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(psMax, r -> List.of(r.getInt("max_age")));

            assertThat(rows).hasSize(1).extracting(r -> (Integer) r.get(0)).containsExactly(40);
        }

        try (PreparedStatement psMin =
                dsl.select().min("age").as("min_age").from("users").build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(psMin, r -> List.of(r.getInt("min_age")));

            assertThat(rows).hasSize(1).extracting(r -> (Integer) r.get(0)).containsExactly(15);
        }
    }

    @Test
    void selectCountDistinct() throws SQLException {
        try (PreparedStatement ps = dsl.select()
                .countDistinct("age")
                .as("unique_ages")
                .from("users")
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getInt("unique_ages")));

            assertThat(rows)
                    .hasSize(1)
                    .extracting(r -> (Integer) r.get(0))
                    .containsExactly(6); // 15, 25, 28, 30, 35, 40
        }
    }

    @Test
    void selectCountWithWhere() throws SQLException {
        try (PreparedStatement ps = dsl.select()
                .count("id")
                .as("active_count")
                .from("users")
                .where()
                .column("active")
                .eq(true)
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getInt("active_count")));

            assertThat(rows).hasSize(1).extracting(r -> (Integer) r.get(0)).containsExactly(7);
        }
    }

    @Test
    void selectMultipleAggregatesWithoutAliases() throws SQLException {
        try (PreparedStatement ps =
                dsl.select().sum("age").max("createdAt").from("users").build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getInt(1), r.getTimestamp(2)));

            assertThat(rows).hasSize(1);
            assertThat(rows.get(0).get(0)).isEqualTo(293); // 30+25+15+35+30+25+40+35+28+30
            assertThat(rows.get(0).get(1)).isNotNull();
        }
    }

    @Test
    void selectMultipleAggregatesWithAliases() throws SQLException {
        try (PreparedStatement ps = dsl.select()
                .sum("age")
                .as("total_age")
                .max("createdAt")
                .as("latest_update")
                .from("users")
                .build(connection)) {
            List<List<Object>> rows =
                    ResultSetUtil.list(ps, r -> List.of(r.getInt("total_age"), r.getTimestamp("latest_update")));

            assertThat(rows).hasSize(1);
            assertThat(rows.get(0).get(0)).isEqualTo(293); // 30+25+15+35+30+25+40+35+28+30
            assertThat(rows.get(0).get(1)).isNotNull();
        }
    }

    @Test
    void selectMultipleAggregatesWithOneAlias() throws SQLException {
        try (PreparedStatement ps =
                dsl.select().sum("age").max("id").as("max_id").from("users").build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getInt(1), r.getInt("max_id")));

            assertThat(rows).hasSize(1);
            assertThat(rows.get(0).get(0)).isEqualTo(293); // 30+25+15+35+30+25+40+35+28+30
            assertThat(rows.get(0).get(1)).isEqualTo(10);
        }
    }

    @Test
    void selectColumn() throws SQLException {
        try (PreparedStatement ps = dsl.select().column("name").from("users").build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name")));

            assertThat(rows)
                    .hasSize(10)
                    .extracting(r -> (String) r.get(0))
                    .contains("John Doe", "Jane Smith", "Bob", "Alice");
        }
    }

    @Test
    void selectColumnWithAlias() throws SQLException {
        try (PreparedStatement ps =
                dsl.select().column("name").as("user_name").from("users").build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("user_name")));

            assertThat(rows).hasSize(10).extracting(r -> (String) r.get(0)).contains("John Doe");
        }
    }

    @Test
    void selectMultipleColumns() throws SQLException {
        try (PreparedStatement ps =
                dsl.select().column("name").column("email").from("users").build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getString("name"), r.getString("email")));

            assertThat(rows)
                    .hasSize(10)
                    .extracting(r -> (String) r.get(0), r -> (String) r.get(1))
                    .contains(tuple("John Doe", "john@example.com"));
        }
    }

    @Test
    void selectMixedColumnsAndAggregates() throws SQLException {
        try (PreparedStatement ps = dsl.select()
                .column("age")
                .count("id")
                .as("user_count")
                .from("users")
                .groupBy()
                .column("age")
                .orderBy()
                .asc("age")
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(ps, r -> List.of(r.getInt("age"), r.getInt("user_count")));

            assertThat(rows)
                    .hasSize(6)
                    .extracting(r -> (Integer) r.get(0), r -> (Integer) r.get(1))
                    .containsExactly(
                            tuple(15, 1), tuple(25, 2), tuple(28, 1), tuple(30, 3), tuple(35, 2), tuple(40, 1));
        }
    }

    @Test
    void selectJsonColumns() throws SQLException {
        // Frank (id=8), Grace (id=9), and Henry (id=10) have JSON data prepopulated
        try (PreparedStatement ps = dsl.select("id", "name", "address", "preferences")
                .from("users")
                .where()
                .column("id")
                .gte(8)
                .build(connection)) {
            List<List<Object>> rows = ResultSetUtil.list(
                    ps,
                    r -> List.of(
                            r.getInt("id"), r.getString("name"), r.getString("address"), r.getString("preferences")));

            assertThat(rows).hasSize(3);

            // Verify Frank's data (id=8)
            List<Object> frankRow = rows.stream()
                    .filter(row -> ((Integer) row.get(0)).equals(8))
                    .findFirst()
                    .orElseThrow();
            assertThat(frankRow.get(1)).isEqualTo("Frank");
            String frankAddress = (String) frankRow.get(2);
            assertThatJson(frankAddress).isEqualToJson("""
                    {
                        "street": "Via Roma 123",
                        "city": "Milan",
                        "zip": "20100",
                        "country": "Italy"
                    }
                    """);
            String frankPreferences = (String) frankRow.get(3);
            assertThatJson(frankPreferences).isEqualToJson("""
                    ["email", "sms"]
                    """);

            // Verify Grace's data (id=9)
            List<Object> graceRow = rows.stream()
                    .filter(row -> ((Integer) row.get(0)).equals(9))
                    .findFirst()
                    .orElseThrow();
            assertThat(graceRow.get(1)).isEqualTo("Grace");
            String graceAddress = (String) graceRow.get(2);
            assertThatJson(graceAddress).isEqualToJson("""
                    {
                        "street": "Via Torino 45",
                        "city": "Rome",
                        "zip": "00100",
                        "country": "Italy"
                    }
                    """);
            String gracePreferences = (String) graceRow.get(3);
            assertThatJson(gracePreferences).isEqualToJson("""
                    ["email", "push"]
                    """);

            // Verify Henry's data (id=10)
            List<Object> henryRow = rows.stream()
                    .filter(row -> ((Integer) row.get(0)).equals(10))
                    .findFirst()
                    .orElseThrow();
            assertThat(henryRow.get(1)).isEqualTo("Henry");
            String henryAddress = (String) henryRow.get(2);
            assertThatJson(henryAddress).isEqualToJson("""
                    {
                        "street": "Corso Vittorio 78",
                        "city": "Turin",
                        "zip": "10100",
                        "country": "Italy"
                    }
                    """);
            String henryPreferences = (String) henryRow.get(3);
            assertThatJson(henryPreferences).isEqualToJson("""
                    ["sms", "push", "phone"]
                    """);
        }
    }
}
