package io.github.massimiliano.fluentsql4j.ast.core.predicate;

import static io.github.massimiliano.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.dsl.select.SelectBuilder;
import io.github.massimiliano.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.massimiliano.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LikeTest {
    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
    }

    // Basic LIKE patterns
    @Test
    void likeExactMatch() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("name")
                .like("John")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("LIKE").contains("?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John");
    }

    @Test
    void likeWildcardStart() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("name")
                .like("%John")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("LIKE").contains("?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "%John");
    }

    @Test
    void likeWildcardEnd() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("name")
                .like("John%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("LIKE").contains("?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John%");
    }

    @Test
    void likeWildcardBoth() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("name")
                .like("%John%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("LIKE").contains("?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "%John%");
    }

    @Test
    void likeUnderscorePattern() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("code")
                .like("ABC_DE")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("LIKE").contains("?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "ABC_DE");
    }

    @Test
    void likeMultipleUnderscores() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .where()
                .column("reference")
                .like("ORD-___-____")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("LIKE").contains("?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "ORD-___-____");
    }

    @Test
    void likeCombinedWildcards() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("title")
                .like("%premium%product%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("LIKE").contains("?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "%premium%product%");
    }

    @Test
    void likeEmptyPattern() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("name")
                .like("")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("LIKE").contains("?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "");
    }

    @Test
    void likeWithSpecialCharacters() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("email")
                .like("%@domain.com%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("LIKE").contains("?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "%@domain.com%");
    }

    @Test
    void likeNumericPattern() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .where()
                .column("order_id")
                .like("2025%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("WHERE").contains("LIKE").contains("?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "2025%");
    }

    // LIKE in different clause positions
    @Test
    void likeInGroupBy() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("users")
                .groupBy()
                .column("category")
                .having()
                .column("category")
                .like("premium%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING").contains("LIKE").contains("?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "premium%");
    }

    // LIKE with logical combinations
    @Test
    void likeWithAnd() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("name")
                .like("%John%")
                .and()
                .column("email")
                .like("%@example.com")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("LIKE")
                .contains("AND")
                .contains("?");
    }

    @Test
    void likeWithOr() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("name")
                .like("%John%")
                .or()
                .column("name")
                .like("%Jane%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("LIKE")
                .contains("OR")
                .contains("?");
    }

    // LIKE predicate object creation
    @Test
    void likePredicateConstruction() {
        Like predicate = new Like(ColumnReference.of("users", "name"), "%test%");

        assertThat(predicate.expression()).isNotNull();
        assertThat(predicate.pattern()).isNotNull();
        assertThat(predicate.pattern()).isEqualTo("%test%");
        assertThat(predicate).isInstanceOf(Predicate.class);
    }

    @Test
    void likePredicateWithLiteral() {
        Like predicate = new Like(Literal.of("SearchValue"), "S%");

        assertThat(predicate.expression()).isNotNull();
        assertThat(predicate.pattern()).isEqualTo("S%");
    }
}
