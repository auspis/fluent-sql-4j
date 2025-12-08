package lan.tlab.r4j.jdsql.dsl.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.predicate.AndOr;
import lan.tlab.r4j.jdsql.ast.core.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.core.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.core.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.clause.LogicalCombinator;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class DeleteBuilderTest {

    private PreparedStatementSpecFactory specFactory;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
    }

    @Test
    void ok() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .where()
                .column("status")
                .eq("inactive")
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("DELETE FROM \"users\" WHERE \"status\" = ?");
        verify(ps).setObject(1, "inactive");
    }

    @Test
    void noWhere() throws SQLException {
        new DeleteBuilder(specFactory, "users").buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("DELETE FROM \"users\"");
    }

    @Test
    void whereWithNumber() throws SQLException {
        new DeleteBuilder(specFactory, "users").where().column("id").eq(42).buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("DELETE FROM \"users\" WHERE \"id\" = ?");
        verify(ps).setObject(1, 42);
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
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("DELETE FROM \"users\" WHERE (\"status\" = ?) AND (\"age\" < ?)");
        verify(ps).setObject(1, "inactive");
        verify(ps).setObject(2, 18);
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
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("DELETE FROM \"users\" WHERE (\"status\" = ?) OR (\"status\" = ?)");
        verify(ps).setObject(1, "deleted");
        verify(ps).setObject(2, "banned");
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
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("DELETE FROM \"users\" WHERE ((\"status\" = ?) AND (\"age\" < ?)) OR (\"role\" = ?)");
        verify(ps).setObject(1, "inactive");
        verify(ps).setObject(2, 18);
        verify(ps).setObject(3, "guest");
    }

    @Test
    void isNull() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .where()
                .column("deleted_at")
                .isNotNull()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("DELETE FROM \"users\" WHERE \"deleted_at\" IS NOT NULL");
    }

    @Test
    void like() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .where()
                .column("email")
                .like("%@temp.com")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("DELETE FROM \"users\" WHERE \"email\" LIKE ?");
        verify(ps).setObject(1, "%@temp.com");
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
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "DELETE FROM \"products\" WHERE ((((\"price\" > ?) AND (\"discount\" < ?)) AND (\"rating\" >= ?)) AND (\"stock\" <= ?)) AND (\"category\" <> ?)");
        verify(ps).setObject(1, 100);
        verify(ps).setObject(2, 50);
        verify(ps).setObject(3, 4);
        verify(ps).setObject(4, 10);
        verify(ps).setObject(5, "deprecated");
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
        assertThat(andOr.operator()).isEqualTo(lan.tlab.r4j.jdsql.ast.core.predicate.LogicalOperator.OR);
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
