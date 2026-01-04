package io.github.massimiliano.fluentsql4j.dsl.delete;

import static io.github.massimiliano.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.AndOr;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Comparison;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Predicate;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Where;
import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.dsl.clause.LogicalCombinator;
import io.github.massimiliano.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.massimiliano.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteBuilderTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void ok() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .where()
                .column("status")
                .eq("inactive")
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\" WHERE \"status\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "inactive");
    }

    @Test
    void noWhere() throws SQLException {
        new DeleteBuilder(specFactory, "users").build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\"");
    }

    @Test
    void whereWithNumber() throws SQLException {
        new DeleteBuilder(specFactory, "users").where().column("id").eq(42).build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\" WHERE \"id\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 42);
    }

    @Test
    void and() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .where()
                .column("status")
                .eq("inactive")
                .and()
                .column("age")
                .lt(18)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\" WHERE (\"status\" = ?) AND (\"age\" < ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "inactive");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 18);
    }

    @Test
    void or() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .where()
                .column("status")
                .eq("deleted")
                .or()
                .column("status")
                .eq("banned")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\" WHERE (\"status\" = ?) OR (\"status\" = ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "deleted");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "banned");
    }

    @Test
    void andOr() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .where()
                .column("status")
                .eq("inactive")
                .and()
                .column("age")
                .lt(18)
                .or()
                .column("role")
                .eq("guest")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo("DELETE FROM \"users\" WHERE ((\"status\" = ?) AND (\"age\" < ?)) OR (\"role\" = ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "inactive");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "guest");
    }

    @Test
    void isNull() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .where()
                .column("deleted_at")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\" WHERE \"deleted_at\" IS NOT NULL");
    }

    @Test
    void like() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .where()
                .column("email")
                .like("%@temp.com")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\" WHERE \"email\" LIKE ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "%@temp.com");
    }

    @Test
    void allComparisonOperators() throws SQLException {
        new DeleteBuilder(specFactory, "products")
                .where()
                .column("price")
                .gt(100)
                .and()
                .column("discount")
                .lt(50)
                .and()
                .column("rating")
                .gte(4)
                .and()
                .column("stock")
                .lte(10)
                .and()
                .column("category")
                .ne("deprecated")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "DELETE FROM \"products\" WHERE ((((\"price\" > ?) AND (\"discount\" < ?)) AND (\"rating\" >= ?)) AND (\"stock\" <= ?)) AND (\"category\" <> ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 50);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 4);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 10);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(5, "deprecated");
    }

    @Test
    void invalidTableName() {
        assertThatThrownBy(() -> new DeleteBuilder(specFactory, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidColumnName() {
        assertThatThrownBy(() -> new DeleteBuilder(specFactory, "users").where().column(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    // Tests for static helper methods
    @Test
    void hasValidConditionReturnsTrueForValidComparison() {
        Where whereWithComparison = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        assertThat(DeleteBuilder.hasValidCondition(whereWithComparison)).isTrue();
    }

    @Test
    void hasValidConditionReturnsFalseForNullPredicate() {
        Where whereWithNull = Where.of(new NullPredicate());

        assertThat(DeleteBuilder.hasValidCondition(whereWithNull)).isFalse();
    }

    @Test
    void combineWithExistingCreatesAndCondition() {
        Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

        Where result = DeleteBuilder.combineWithExisting(existingWhere, newCondition, LogicalCombinator.AND);

        assertThat(result.condition()).isInstanceOf(AndOr.class);
    }

    @Test
    void combineWithExistingCreatesOrCondition() {
        Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

        Where result = DeleteBuilder.combineWithExisting(existingWhere, newCondition, LogicalCombinator.OR);

        assertThat(result.condition()).isInstanceOf(AndOr.class);
        AndOr andOr = (AndOr) result.condition();
        assertThat(andOr.operator())
                .isEqualTo(io.github.massimiliano.fluentsql4j.ast.core.predicate.LogicalOperator.OR);
    }

    @Test
    void combineConditionsWithNullWhereCreatesNewCondition() {
        Predicate condition = Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John"));

        Where result = DeleteBuilder.combineConditions(null, condition, LogicalCombinator.AND);

        assertThat(result.condition()).isEqualTo(condition);
    }

    @Test
    void combineConditionsWithValidWhereCreatesCombinedCondition() {
        Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

        Where result = DeleteBuilder.combineConditions(existingWhere, newCondition, LogicalCombinator.OR);

        assertThat(result.condition()).isInstanceOf(AndOr.class);
    }

    @Test
    void combineConditionsWithNullPredicateCreatesNewCondition() {
        Where existingWhere = Where.of(new NullPredicate());

        Predicate newCondition = Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John"));

        Where result = DeleteBuilder.combineConditions(existingWhere, newCondition, LogicalCombinator.AND);

        assertThat(result.condition()).isEqualTo(newCondition);
    }
}
