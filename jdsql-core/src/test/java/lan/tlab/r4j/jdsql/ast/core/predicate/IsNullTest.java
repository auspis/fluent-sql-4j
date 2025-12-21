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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL");
    }

    @Test
    void isNullOnName() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("description")
                .isNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL");
    }

    @Test
    void isNullOnId() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .where()
                .column("parent_order_id")
                .isNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL");
    }

    @Test
    void isNullOnTimestamp() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("events")
                .where()
                .column("deleted_at")
                .isNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL");
    }

    @Test
    void isNullOnNumericColumn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("data")
                .where()
                .column("value")
                .isNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("IS NULL");
    }

    @Test
    void isNullOnBooleanColumn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("settings")
                .where()
                .column("enabled")
                .isNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
