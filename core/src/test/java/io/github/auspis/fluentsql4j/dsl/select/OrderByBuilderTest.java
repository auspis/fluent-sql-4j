package io.github.auspis.fluentsql4j.dsl.select;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

class OrderByBuilderTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void buildConnection() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .orderBy()
                .asc("created_at")
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" ORDER BY \"created_at\" ASC");
    }

    @Test
    void buildConnectionWithMultipleSortings() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .orderBy()
                .asc("created_at")
                .desc("status")
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT * FROM \"orders\" ORDER BY \"created_at\" ASC, \"status\" DESC");
    }

    @Test
    void buildConnectionWithJoinAndExplicitAlias() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .orderBy()
                .asc("c", "name")
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" ORDER BY \"c\".\"name\" ASC");
    }

    @Test
    void fetchDelegatesToParent() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .orderBy()
                .asc("created_at")
                .fetch(10)
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT * FROM \"orders\" ORDER BY \"created_at\" ASC FETCH NEXT 10 ROWS ONLY");
    }

    @Test
    void offsetDelegatesToParent() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .orderBy()
                .asc("created_at")
                .offset(20)
                .fetch(10)
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"orders\" ORDER BY \"created_at\" ASC OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY");
    }

    @Test
    void doneReturnsParentBuilder() throws SQLException {
        SelectBuilder result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .orderBy()
                .desc("status")
                .done();

        assertThat(result).isInstanceOf(SelectBuilder.class);
        assertThat(result.build(sqlCaptureHelper.getConnection())).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" ORDER BY \"status\" DESC");
    }

    @Test
    void buildWithoutSortingsThrowsException() {
        OrderByBuilder builder =
                new SelectBuilder(specFactory, "*").from("orders").orderBy();

        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("ORDER BY must contain at least one sorting column");
    }

    @Test
    void buildConnectionWithoutSortingsThrowsException() {
        OrderByBuilder builder =
                new SelectBuilder(specFactory, "*").from("orders").orderBy();

        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("ORDER BY must contain at least one sorting column");
    }

    @Nested
    class AscValidation {

        @ParameterizedTest
        @MethodSource("invalidColumns")
        void rejectsInvalidSingleColumn(String column, String expectedMessage) {
            OrderByBuilder builder =
                    new SelectBuilder(specFactory, "*").from("orders").orderBy();

            assertThatThrownBy(() -> builder.asc(column))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(expectedMessage);
        }

        static Stream<Arguments> invalidColumns() {
            return Stream.of(
                    Arguments.of(null, "ORDER BY column cannot be null or empty"),
                    Arguments.of(" ", "ORDER BY column cannot be null or empty"),
                    Arguments.of("orders.created_at", "ORDER BY column must not contain dot notation"));
        }

        @ParameterizedTest
        @MethodSource("invalidAliasColumns")
        void rejectsInvalidAliasColumn(String alias, String column, String expectedMessage) {
            OrderByBuilder builder =
                    new SelectBuilder(specFactory, "*").from("orders").as("o").orderBy();

            assertThatThrownBy(() -> builder.asc(alias, column))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(expectedMessage);
        }

        static Stream<Arguments> invalidAliasColumns() {
            return Stream.of(
                    Arguments.of(null, "created_at", "Table reference cannot be null or empty"),
                    Arguments.of("o.x", "created_at", "Table reference must not contain dot: 'o.x'"),
                    Arguments.of("o", null, "Column name cannot be null or empty"),
                    Arguments.of("o", "c.name", "Column name must not contain dot"));
        }
    }

    @Nested
    class DescValidation {

        @ParameterizedTest
        @MethodSource("invalidColumns")
        void rejectsInvalidSingleColumn(String column, String expectedMessage) {
            OrderByBuilder builder =
                    new SelectBuilder(specFactory, "*").from("orders").orderBy();

            assertThatThrownBy(() -> builder.desc(column))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(expectedMessage);
        }

        static Stream<Arguments> invalidColumns() {
            return Stream.of(
                    Arguments.of(null, "ORDER BY column cannot be null or empty"),
                    Arguments.of(" ", "ORDER BY column cannot be null or empty"),
                    Arguments.of("orders.status", "ORDER BY column must not contain dot notation"));
        }

        @ParameterizedTest
        @MethodSource("invalidAliasColumns")
        void rejectsInvalidAliasColumn(String alias, String column, String expectedMessage) {
            OrderByBuilder builder =
                    new SelectBuilder(specFactory, "*").from("orders").as("o").orderBy();

            assertThatThrownBy(() -> builder.desc(alias, column))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(expectedMessage);
        }

        static Stream<Arguments> invalidAliasColumns() {
            return Stream.of(
                    Arguments.of(null, "status", "Table reference cannot be null or empty"),
                    Arguments.of("o.x", "status", "Table reference must not contain dot: 'o.x'"),
                    Arguments.of("o", "", "Column name cannot be null or empty"),
                    Arguments.of("o", "c.status", "Column name must not contain dot"));
        }
    }
}
