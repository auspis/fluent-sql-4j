package io.github.auspis.fluentsql4j.dsl.select;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SelectBuilderOrderByTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void singleColumnAscending() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .orderBy()
                .asc("customer_id")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" ORDER BY \"customer_id\" ASC");
    }

    @Test
    void singleColumnDescending() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .orderBy()
                .desc("status")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" ORDER BY \"status\" DESC");
    }

    @Test
    void withTableAlias() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .orderBy()
                .asc("customer_id")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" AS o ORDER BY \"customer_id\" ASC");
    }

    @Test
    void withExplicitAlias() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .orderBy()
                .asc("o", "customer_id")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" AS o ORDER BY \"customer_id\" ASC");
    }

    @Test
    void withExplicitAliasDescending() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .orderBy()
                .desc("o", "status")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" AS o ORDER BY \"status\" DESC");
    }

    @Test
    void multipleColumnsWithExplicitAliases() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .orderBy()
                .asc("o", "customer_id")
                .desc("o", "created_at")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT * FROM \"orders\" AS o ORDER BY \"customer_id\" ASC, \"created_at\" DESC");
    }

    @Test
    void withJoin() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .orderBy()
                .asc("c", "name")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" ORDER BY \"c\".\"name\" ASC");
    }

    @Test
    void withJoinMultipleColumns() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .orderBy()
                .asc("o", "created_at")
                .desc("c", "name")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" ORDER BY \"o\".\"created_at\" ASC, \"c\".\"name\" DESC");
    }

    @Test
    void withWhereAndJoin() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .where()
                .column("o", "status")
                .eq("completed")
                .orderBy()
                .asc("c", "name")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" WHERE \"o\".\"status\" = ? ORDER BY \"c\".\"name\" ASC");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "completed");
    }

    @Test
    void complexQueryWithGroupByAndOrderBy() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "customer_id", "status")
                .from("orders")
                .as("o")
                .groupBy()
                .column("customer_id")
                .column("status")
                .build()
                .orderBy()
                .asc("customer_id")
                .desc("status")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT \"customer_id\", \"status\" FROM \"orders\" AS o GROUP BY \"customer_id\", \"status\" ORDER BY \"customer_id\" ASC, \"status\" DESC");
    }

    // Validation tests

    @Nested
    class AscValidation {

        @ParameterizedTest
        @MethodSource("invalidSingleColumnArgs")
        void rejectsInvalidSingleColumn(String column, String expectedMessage) {
            OrderByBuilder orderBy =
                    new SelectBuilder(specFactory, "*").from("orders").orderBy();
            assertThatThrownBy(() -> orderBy.asc(column))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(expectedMessage);
        }

        static Stream<Arguments> invalidSingleColumnArgs() {
            return Stream.of(
                    Arguments.of(null, "ORDER BY column cannot be null or empty"),
                    Arguments.of("  ", "ORDER BY column cannot be null or empty"),
                    Arguments.of("orders.customer_id", "ORDER BY column must not contain dot notation"));
        }

        @ParameterizedTest
        @MethodSource("invalidAliasArgs")
        void rejectsInvalidAlias(String alias, String expectedMessage) {
            OrderByBuilder orderBy =
                    new SelectBuilder(specFactory, "*").from("orders").as("o").orderBy();
            assertThatThrownBy(() -> orderBy.asc(alias, "customer_id"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(expectedMessage);
        }

        static Stream<Arguments> invalidAliasArgs() {
            return Stream.of(
                    Arguments.of(null, "Table reference cannot be null or empty"),
                    Arguments.of("  ", "Table reference cannot be null or empty"),
                    Arguments.of("o.x", "Table reference must not contain dot: 'o.x'"));
        }

        @ParameterizedTest
        @MethodSource("invalidColumnWithAliasArgs")
        void rejectsInvalidColumnWhenAliasProvided(String column, String expectedMessage) {
            OrderByBuilder orderBy =
                    new SelectBuilder(specFactory, "*").from("orders").as("o").orderBy();
            assertThatThrownBy(() -> orderBy.asc("o", column))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(expectedMessage);
        }

        static Stream<Arguments> invalidColumnWithAliasArgs() {
            return Stream.of(
                    Arguments.of(null, "Column name cannot be null or empty"),
                    Arguments.of("  ", "Column name cannot be null or empty"),
                    Arguments.of("x.customer_id", "Column name must not contain dot"));
        }
    }

    @Nested
    class DescValidation {

        @ParameterizedTest
        @MethodSource("invalidSingleColumnArgs")
        void rejectsInvalidSingleColumn(String column, String expectedMessage) {
            OrderByBuilder orderBy =
                    new SelectBuilder(specFactory, "*").from("orders").orderBy();
            assertThatThrownBy(() -> orderBy.desc(column))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(expectedMessage);
        }

        static Stream<Arguments> invalidSingleColumnArgs() {
            return Stream.of(
                    Arguments.of(null, "ORDER BY column cannot be null or empty"),
                    Arguments.of("orders.status", "ORDER BY column must not contain dot notation"));
        }

        @ParameterizedTest
        @MethodSource("invalidTwoArgCases")
        void rejectsInvalidArguments(String alias, String column, String expectedMessage) {
            OrderByBuilder orderBy =
                    new SelectBuilder(specFactory, "*").from("orders").as("o").orderBy();
            assertThatThrownBy(() -> orderBy.desc(alias, column))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(expectedMessage);
        }

        static Stream<Arguments> invalidTwoArgCases() {
            return Stream.of(
                    Arguments.of(null, "customer_id", "Table reference cannot be null or empty"),
                    Arguments.of("o.x", "customer_id", "Table reference must not contain dot: 'o.x'"),
                    Arguments.of("o", "x.status", "Column name must not contain dot"));
        }
    }

    @Test
    void emptyOrderByThrowsException() {
        SelectBuilder builder = new SelectBuilder(specFactory, "*").from("orders");
        OrderByBuilder orderBy = builder.orderBy();
        assertThatThrownBy(() -> orderBy.build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ORDER BY must contain at least one sorting column");
    }
}
