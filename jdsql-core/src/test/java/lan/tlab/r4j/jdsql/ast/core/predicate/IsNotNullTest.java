package lan.tlab.r4j.jdsql.ast.core.predicate;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.select.SelectBuilder;
import lan.tlab.r4j.jdsql.plugin.util.StandardSqlUtil;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IsNotNullTest {
    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
    }

    // Basic IS NOT NULL tests
    @Test
    void isNotNullSimple() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NOT NULL");
    }

    @Test
    void isNotNullOnName() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("description")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NOT NULL");
    }

    @Test
    void isNotNullOnId() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .where()
                .column("parent_order_id")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NOT NULL");
    }

    @Test
    void isNotNullOnTimestamp() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("events")
                .where()
                .column("deleted_at")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NOT NULL");
    }

    @Test
    void isNotNullOnNumericColumn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("data")
                .where()
                .column("value")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NOT NULL");
    }

    @Test
    void isNotNullOnBooleanColumn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("settings")
                .where()
                .column("enabled")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NOT NULL");
    }

    // IS NOT NULL with logical operators
    @Test
    void isNotNullWithAnd() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .isNotNull()
                .and()
                .column("phone")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("IS NOT NULL")
                .contains("AND");
    }

    @Test
    void isNotNullWithOr() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .isNotNull()
                .or()
                .column("phone")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("IS NOT NULL")
                .contains("OR");
    }

    @Test
    void isNotNullWithAndComparison() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .isNotNull()
                .and()
                .column("age")
                .gt(18)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("IS NOT NULL")
                .contains("AND");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
    }

    @Test
    void isNotNullWithOrComparison() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("discount")
                .isNotNull()
                .or()
                .column("price")
                .lt(50)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("IS NOT NULL")
                .contains("OR");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 50);
    }

    // IS NOT NULL with IN, LIKE predicates
    @Test
    void isNotNullWithIn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("status")
                .isNotNull()
                .and()
                .column("category")
                .in("A", "B", "C")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("IS NOT NULL")
                .contains("AND")
                .contains("IN");
    }

    @Test
    void isNotNullWithLike() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .isNotNull()
                .and()
                .column("name")
                .like("%John%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("IS NOT NULL")
                .contains("AND")
                .contains("LIKE");
    }

    @Test
    void isNotNullVsIsNull() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .isNotNull()
                .or()
                .column("phone")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("IS NOT NULL")
                .contains("OR")
                .contains("IS NULL");
    }

    // IS NOT NULL predicate object construction
    @Test
    void isNotNullPredicateConstruction() {
        IsNotNull predicate = new IsNotNull(ColumnReference.of("users", "email"));

        assertThat(predicate.expression()).isNotNull();
        assertThat(predicate).isInstanceOf(Predicate.class);
    }

    @Test
    void isNotNullPredicateWithLiteral() {
        IsNotNull predicate = new IsNotNull(Literal.of("test"));

        assertThat(predicate.expression()).isNotNull();
    }

    @Test
    void isNotNullPredicateWithQualifiedColumn() {
        IsNotNull predicate = new IsNotNull(ColumnReference.of("orders", "deleted_at"));

        assertThat(predicate.expression()).isNotNull();
    }
}
