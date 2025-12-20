package lan.tlab.r4j.jdsql.ast.core.predicate;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.select.SelectBuilder;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IsNotNullTest {
    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
    }

    // Basic IS NOT NULL tests
    @Test
    void isNotNullSimple() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .isNotNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NOT NULL");
    }

    @Test
    void isNotNullOnName() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("description")
                .isNotNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NOT NULL");
    }

    @Test
    void isNotNullOnId() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .where()
                .column("parent_order_id")
                .isNotNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NOT NULL");
    }

    @Test
    void isNotNullOnTimestamp() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("events")
                .where()
                .column("deleted_at")
                .isNotNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NOT NULL");
    }

    @Test
    void isNotNullOnNumericColumn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("data")
                .where()
                .column("value")
                .isNotNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NOT NULL");
    }

    @Test
    void isNotNullOnBooleanColumn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("settings")
                .where()
                .column("enabled")
                .isNotNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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

        assert predicate.expression() != null;
        assert predicate instanceof Predicate;
    }

    @Test
    void isNotNullPredicateWithLiteral() {
        IsNotNull predicate = new IsNotNull(Literal.of("test"));

        assert predicate.expression() != null;
    }

    @Test
    void isNotNullPredicateWithQualifiedColumn() {
        IsNotNull predicate = new IsNotNull(ColumnReference.of("orders", "deleted_at"));

        assert predicate.expression() != null;
    }
}
