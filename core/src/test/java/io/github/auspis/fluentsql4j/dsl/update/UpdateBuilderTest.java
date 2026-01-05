package io.github.auspis.fluentsql4j.dsl.update;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.predicate.AndOr;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.clause.LogicalCombinator;
import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateBuilderTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void singleSet() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("name", "John")
                .where()
                .column("id")
                .eq(1)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("UPDATE \"users\" SET \"name\" = ? WHERE \"id\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 1);
    }

    @Test
    void multipleSets() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("name", "John")
                .set("age", 30)
                .where()
                .column("id")
                .eq(1)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("UPDATE \"users\" SET \"name\" = ?, \"age\" = ? WHERE \"id\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 30);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 1);
    }

    @Test
    void noWhere() throws SQLException {
        new UpdateBuilder(specFactory, "users").set("status", "active").build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("UPDATE \"users\" SET \"status\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "active");
    }

    @Test
    void whereWithNumber() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("age", 25)
                .where()
                .column("id")
                .eq(42)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("UPDATE \"users\" SET \"age\" = ? WHERE \"id\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 25);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 42);
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
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo("UPDATE \"users\" SET \"status\" = ? WHERE (\"age\" < ?) AND (\"verified\" = ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "inactive");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, false);
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
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo("UPDATE \"users\" SET \"status\" = ? WHERE (\"status\" = ?) OR (\"status\" = ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "deleted");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "banned");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "inactive");
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
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "UPDATE \"users\" SET \"status\" = ? WHERE ((\"age\" < ?) AND (\"verified\" = ?)) OR (\"deleted_at\" IS NOT NULL)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "inactive");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, false);
    }

    @Test
    void isNull() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("deleted_at", (String) null)
                .where()
                .column("status")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("UPDATE \"users\" SET \"deleted_at\" = ? WHERE \"status\" IS NULL");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, null);
    }

    @Test
    void like() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("status", "verified")
                .where()
                .column("email")
                .like("%@example.com")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("UPDATE \"users\" SET \"status\" = ? WHERE \"email\" LIKE ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "verified");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "%@example.com");
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
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "UPDATE \"products\" SET \"discount\" = ? WHERE ((((\"price\" > ?) AND (\"stock\" < ?)) AND (\"rating\" >= ?)) AND (\"views\" <= ?)) AND (\"category\" <> ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 20);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 50);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 4);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(5, 1000);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(6, "deprecated");
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
                        .build(sqlCaptureHelper.getConnection()))
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
        assertThat(andOr.operator()).isEqualTo(io.github.auspis.fluentsql4j.ast.core.predicate.LogicalOperator.OR);
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
