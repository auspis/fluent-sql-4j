package io.github.auspis.fluentsql4j.ast.core.predicate;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.select.SelectBuilder;
import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IsNullTest {
    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
    }

    // Basic IS NULL tests
    @Test
    void isNullSimple() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL");
    }

    @Test
    void isNullOnName() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("description")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL");
    }

    @Test
    void isNullOnId() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .where()
                .column("parent_order_id")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL");
    }

    @Test
    void isNullOnTimestamp() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("events")
                .where()
                .column("deleted_at")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL");
    }

    @Test
    void isNullOnNumericColumn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("data")
                .where()
                .column("value")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL");
    }

    @Test
    void isNullOnBooleanColumn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("settings")
                .where()
                .column("enabled")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL");
    }

    // IS NULL with logical operators
    @Test
    void isNullWithAnd() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .isNull()
                .and()
                .column("phone")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL").contains("AND");
    }

    @Test
    void isNullWithOr() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .isNull()
                .or()
                .column("phone")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL").contains("OR");
    }

    @Test
    void isNullWithAndComparison() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .isNull()
                .and()
                .column("age")
                .gt(18)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL").contains("AND");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
    }

    @Test
    void isNullWithOrComparison() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("discount")
                .isNull()
                .or()
                .column("price")
                .lt(50)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL").contains("OR");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 50);
    }

    // IS NULL with IN, LIKE predicates
    @Test
    void isNullWithIn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("status")
                .isNull()
                .or()
                .column("category")
                .in("A", "B", "C")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("IS NULL")
                .contains("OR")
                .contains("IN");
    }

    @Test
    void isNullWithLike() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .isNull()
                .or()
                .column("name")
                .like("%John%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("IS NULL")
                .contains("OR")
                .contains("LIKE");
    }

    // IS NULL predicate object construction
    @Test
    void isNullPredicateConstruction() {
        IsNull predicate = new IsNull(ColumnReference.of("users", "email"));

        assertThat(predicate.expression()).isNotNull();
        assertThat(predicate).isInstanceOf(Predicate.class);
    }

    @Test
    void isNullPredicateWithLiteral() {
        IsNull predicate = new IsNull(Literal.of("test"));

        assertThat(predicate.expression()).isNotNull();
    }

    @Test
    void isNullPredicateWithQualifiedColumn() {
        IsNull predicate = new IsNull(ColumnReference.of("orders", "deleted_at"));

        assertThat(predicate.expression()).isNotNull();
    }
}
