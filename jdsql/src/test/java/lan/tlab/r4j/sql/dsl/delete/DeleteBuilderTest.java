package lan.tlab.r4j.sql.dsl.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.common.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.common.predicate.Predicate;
import lan.tlab.r4j.sql.ast.common.predicate.logical.AndOr;
import lan.tlab.r4j.sql.ast.dql.clause.Where;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.LogicalCombinator;
import lan.tlab.r4j.sql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteBuilderTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void ok() {
        String result = new DeleteBuilder(renderer, "users")
                .where()
                .column("status")
                .eq("inactive")
                .build();
        assertThat(result).isEqualTo("DELETE FROM \"users\" WHERE \"users\".\"status\" = 'inactive'");
    }

    @Test
    void noWhere() {
        String result = new DeleteBuilder(renderer, "users").build();
        assertThat(result).isEqualTo("DELETE FROM \"users\"");
    }

    @Test
    void whereWithNumber() {
        String result =
                new DeleteBuilder(renderer, "users").where().column("id").eq(42).build();
        assertThat(result).isEqualTo("DELETE FROM \"users\" WHERE \"users\".\"id\" = 42");
    }

    @Test
    void and() {
        String result = new DeleteBuilder(renderer, "users")
                .where()
                .column("status")
                .eq("inactive")
                .and()
                .column("age")
                .lt(18)
                .build();

        assertThat(result)
                .isEqualTo(
                        "DELETE FROM \"users\" WHERE (\"users\".\"status\" = 'inactive') AND (\"users\".\"age\" < 18)");
    }

    @Test
    void or() {
        String result = new DeleteBuilder(renderer, "users")
                .where()
                .column("status")
                .eq("deleted")
                .or()
                .column("status")
                .eq("banned")
                .build();

        assertThat(result)
                .isEqualTo(
                        "DELETE FROM \"users\" WHERE (\"users\".\"status\" = 'deleted') OR (\"users\".\"status\" = 'banned')");
    }

    @Test
    void andOr() {
        String result = new DeleteBuilder(renderer, "users")
                .where()
                .column("status")
                .eq("inactive")
                .and()
                .column("age")
                .lt(18)
                .or()
                .column("role")
                .eq("guest")
                .build();

        assertThat(result)
                .isEqualTo(
                        "DELETE FROM \"users\" WHERE ((\"users\".\"status\" = 'inactive') AND (\"users\".\"age\" < 18)) OR (\"users\".\"role\" = 'guest')");
    }

    @Test
    void isNull() {
        String result = new DeleteBuilder(renderer, "users")
                .where()
                .column("deleted_at")
                .isNotNull()
                .build();

        assertThat(result).isEqualTo("DELETE FROM \"users\" WHERE \"users\".\"deleted_at\" IS NOT NULL");
    }

    @Test
    void like() {
        String result = new DeleteBuilder(renderer, "users")
                .where()
                .column("email")
                .like("%@temp.com")
                .build();

        assertThat(result).isEqualTo("DELETE FROM \"users\" WHERE \"users\".\"email\" LIKE '%@temp.com'");
    }

    @Test
    void allComparisonOperators() {
        String result = new DeleteBuilder(renderer, "products")
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
                .build();

        assertThat(result)
                .isEqualTo(
                        "DELETE FROM \"products\" WHERE ((((\"products\".\"price\" > 100) AND (\"products\".\"discount\" < 50)) AND (\"products\".\"rating\" >= 4)) AND (\"products\".\"stock\" <= 10)) AND (\"products\".\"category\" != 'deprecated')");
    }

    @Test
    void invalidTableName() {
        assertThatThrownBy(() -> new DeleteBuilder(renderer, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidColumnName() {
        assertThatThrownBy(() -> new DeleteBuilder(renderer, "users").where().column(""))
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
        assertThat(andOr.operator()).isEqualTo(lan.tlab.r4j.sql.ast.common.predicate.logical.LogicalOperator.OR);
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
