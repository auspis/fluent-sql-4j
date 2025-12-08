package lan.tlab.r4j.jdsql.dsl.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.common.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.common.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.common.predicate.logical.AndOr;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.clause.LogicalCombinator;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UpdateBuilderTest {

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
    void singleSet() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("name", "John")
                .where()
                .column("id")
                .eq(1)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("UPDATE \"users\" SET \"name\" = ? WHERE \"id\" = ?");
        verify(ps).setObject(1, "John");
        verify(ps).setObject(2, 1);
    }

    @Test
    void multipleSets() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("name", "John")
                .set("age", 30)
                .where()
                .column("id")
                .eq(1)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("UPDATE \"users\" SET \"name\" = ?, \"age\" = ? WHERE \"id\" = ?");
        verify(ps).setObject(1, "John");
        verify(ps).setObject(2, 30);
        verify(ps).setObject(3, 1);
    }

    @Test
    void noWhere() throws SQLException {
        new UpdateBuilder(specFactory, "users").set("status", "active").buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("UPDATE \"users\" SET \"status\" = ?");
        verify(ps).setObject(1, "active");
    }

    @Test
    void whereWithNumber() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("age", 25)
                .where()
                .column("id")
                .eq(42)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("UPDATE \"users\" SET \"age\" = ? WHERE \"id\" = ?");
        verify(ps).setObject(1, 25);
        verify(ps).setObject(2, 42);
    }

    @Test
    void and() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("status", "inactive")
                .where()
                .column("age")
                .lt(18)
                .and()
                .column("verified")
                .eq(false)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("UPDATE \"users\" SET \"status\" = ? WHERE (\"age\" < ?) AND (\"verified\" = ?)");
        verify(ps).setObject(1, "inactive");
        verify(ps).setObject(2, 18);
        verify(ps).setObject(3, false);
    }

    @Test
    void or() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("status", "deleted")
                .where()
                .column("status")
                .eq("banned")
                .or()
                .column("status")
                .eq("inactive")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("UPDATE \"users\" SET \"status\" = ? WHERE (\"status\" = ?) OR (\"status\" = ?)");
        verify(ps).setObject(1, "deleted");
        verify(ps).setObject(2, "banned");
        verify(ps).setObject(3, "inactive");
    }

    @Test
    void andOr() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("status", "inactive")
                .where()
                .column("age")
                .lt(18)
                .and()
                .column("verified")
                .eq(false)
                .or()
                .column("deleted_at")
                .isNotNull()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "UPDATE \"users\" SET \"status\" = ? WHERE ((\"age\" < ?) AND (\"verified\" = ?)) OR (\"deleted_at\" IS NOT NULL)");
        verify(ps).setObject(1, "inactive");
        verify(ps).setObject(2, 18);
        verify(ps).setObject(3, false);
    }

    @Test
    void isNull() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("deleted_at", (String) null)
                .where()
                .column("status")
                .isNull()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("UPDATE \"users\" SET \"deleted_at\" = ? WHERE \"status\" IS NULL");
        verify(ps).setObject(1, null);
    }

    @Test
    void like() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("status", "verified")
                .where()
                .column("email")
                .like("%@example.com")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("UPDATE \"users\" SET \"status\" = ? WHERE \"email\" LIKE ?");
        verify(ps).setObject(1, "verified");
        verify(ps).setObject(2, "%@example.com");
    }

    @Test
    void allComparisonOperators() throws SQLException {
        new UpdateBuilder(specFactory, "products")
                .set("discount", 20)
                .where()
                .column("price")
                .gt(100)
                .and()
                .column("stock")
                .lt(50)
                .and()
                .column("rating")
                .gte(4)
                .and()
                .column("views")
                .lte(1000)
                .and()
                .column("category")
                .ne("deprecated")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "UPDATE \"products\" SET \"discount\" = ? WHERE ((((\"price\" > ?) AND (\"stock\" < ?)) AND (\"rating\" >= ?)) AND (\"views\" <= ?)) AND (\"category\" <> ?)");
        verify(ps).setObject(1, 20);
        verify(ps).setObject(2, 100);
        verify(ps).setObject(3, 50);
        verify(ps).setObject(4, 4);
        verify(ps).setObject(5, 1000);
        verify(ps).setObject(6, "deprecated");
    }

    @Test
    void invalidTableName() {
        assertThatThrownBy(() -> new UpdateBuilder(specFactory, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidColumnName() {
        assertThatThrownBy(() -> new UpdateBuilder(specFactory, "users").set("", "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void buildWithoutSetThrowsException() throws SQLException {
        assertThatThrownBy(() -> new UpdateBuilder(specFactory, "users")
                        .where()
                        .column("id")
                        .eq(1)
                        .buildPreparedStatement(connection))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("At least one SET clause must be specified");
    }

    @Test
    void hasValidConditionReturnsTrueForValidComparison() {
        Where whereWithComparison = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        assertThat(UpdateBuilder.hasValidCondition(whereWithComparison)).isTrue();
    }

    @Test
    void hasValidConditionReturnsFalseForNullPredicate() {
        Where whereWithNull = Where.of(new NullPredicate());

        assertThat(UpdateBuilder.hasValidCondition(whereWithNull)).isFalse();
    }

    @Test
    void combineWithExistingCreatesAndCondition() {
        Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

        Where result = UpdateBuilder.combineWithExisting(existingWhere, newCondition, LogicalCombinator.AND);

        assertThat(result.condition()).isInstanceOf(AndOr.class);
    }

    @Test
    void combineWithExistingCreatesOrCondition() {
        Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

        Where result = UpdateBuilder.combineWithExisting(existingWhere, newCondition, LogicalCombinator.OR);

        assertThat(result.condition()).isInstanceOf(AndOr.class);
        AndOr andOr = (AndOr) result.condition();
        assertThat(andOr.operator()).isEqualTo(lan.tlab.r4j.jdsql.ast.common.predicate.logical.LogicalOperator.OR);
    }

    @Test
    void combineConditionsWithNullWhereCreatesNewCondition() {
        Predicate condition = Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John"));

        Where result = UpdateBuilder.combineConditions(null, condition, LogicalCombinator.AND);

        assertThat(result.condition()).isEqualTo(condition);
    }

    @Test
    void combineConditionsWithValidWhereCreatesCombinedCondition() {
        Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

        Where result = UpdateBuilder.combineConditions(existingWhere, newCondition, LogicalCombinator.OR);

        assertThat(result.condition()).isInstanceOf(AndOr.class);
    }

    @Test
    void combineConditionsWithNullPredicateCreatesNewCondition() {
        Where existingWhere = Where.of(new NullPredicate());

        Predicate newCondition = Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John"));

        Where result = UpdateBuilder.combineConditions(existingWhere, newCondition, LogicalCombinator.AND);

        assertThat(result.condition()).isEqualTo(newCondition);
    }
}
