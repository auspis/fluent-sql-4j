package lan.tlab.r4j.sql.dsl.update;

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
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateBuilderTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void singleSet() {
        String result = new UpdateBuilder(renderer, "users")
                .set("name", "John")
                .where("id")
                .eq(1)
                .build();
        assertThat(result).isEqualTo("UPDATE \"users\" SET \"name\" = 'John' WHERE \"users\".\"id\" = 1");
    }

    @Test
    void multipleSets() {
        String result = new UpdateBuilder(renderer, "users")
                .set("name", "John")
                .set("age", 30)
                .where("id")
                .eq(1)
                .build();
        assertThat(result).isEqualTo("UPDATE \"users\" SET \"name\" = 'John', \"age\" = 30 WHERE \"users\".\"id\" = 1");
    }

    @Test
    void noWhere() {
        String result =
                new UpdateBuilder(renderer, "users").set("status", "active").build();
        assertThat(result).isEqualTo("UPDATE \"users\" SET \"status\" = 'active'");
    }

    @Test
    void whereWithNumber() {
        String result = new UpdateBuilder(renderer, "users")
                .set("age", 25)
                .where("id")
                .eq(42)
                .build();
        assertThat(result).isEqualTo("UPDATE \"users\" SET \"age\" = 25 WHERE \"users\".\"id\" = 42");
    }

    @Test
    void and() {
        String result = new UpdateBuilder(renderer, "users")
                .set("status", "inactive")
                .where("age")
                .lt(18)
                .and("verified")
                .eq(false)
                .build();

        assertThat(result)
                .isEqualTo(
                        "UPDATE \"users\" SET \"status\" = 'inactive' WHERE (\"users\".\"age\" < 18) AND (\"users\".\"verified\" = false)");
    }

    @Test
    void or() {
        String result = new UpdateBuilder(renderer, "users")
                .set("status", "deleted")
                .where("status")
                .eq("banned")
                .or("status")
                .eq("inactive")
                .build();

        assertThat(result)
                .isEqualTo(
                        "UPDATE \"users\" SET \"status\" = 'deleted' WHERE (\"users\".\"status\" = 'banned') OR (\"users\".\"status\" = 'inactive')");
    }

    @Test
    void andOr() {
        String result = new UpdateBuilder(renderer, "users")
                .set("status", "inactive")
                .where("age")
                .lt(18)
                .and("verified")
                .eq(false)
                .or("deleted_at")
                .isNotNull()
                .build();

        assertThat(result)
                .isEqualTo(
                        "UPDATE \"users\" SET \"status\" = 'inactive' WHERE ((\"users\".\"age\" < 18) AND (\"users\".\"verified\" = false)) OR (\"users\".\"deleted_at\" IS NOT NULL)");
    }

    @Test
    void isNull() {
        String result = new UpdateBuilder(renderer, "users")
                .set("deleted_at", (String) null)
                .where("status")
                .isNull()
                .build();

        assertThat(result).isEqualTo("UPDATE \"users\" SET \"deleted_at\" = null WHERE \"users\".\"status\" IS NULL");
    }

    @Test
    void like() {
        String result = new UpdateBuilder(renderer, "users")
                .set("status", "verified")
                .where("email")
                .like("%@example.com")
                .build();

        assertThat(result)
                .isEqualTo(
                        "UPDATE \"users\" SET \"status\" = 'verified' WHERE \"users\".\"email\" LIKE '%@example.com'");
    }

    @Test
    void allComparisonOperators() {
        String result = new UpdateBuilder(renderer, "products")
                .set("discount", 20)
                .where("price")
                .gt(100)
                .and("stock")
                .lt(50)
                .and("rating")
                .gte(4)
                .and("views")
                .lte(1000)
                .and("category")
                .ne("deprecated")
                .build();

        assertThat(result)
                .isEqualTo(
                        "UPDATE \"products\" SET \"discount\" = 20 WHERE ((((\"products\".\"price\" > 100) AND (\"products\".\"stock\" < 50)) AND (\"products\".\"rating\" >= 4)) AND (\"products\".\"views\" <= 1000)) AND (\"products\".\"category\" != 'deprecated')");
    }

    @Test
    void invalidTableName() {
        assertThatThrownBy(() -> new UpdateBuilder(renderer, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidColumnName() {
        assertThatThrownBy(() -> new UpdateBuilder(renderer, "users").set("", "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void buildWithoutSetThrowsException() {
        assertThatThrownBy(() ->
                        new UpdateBuilder(renderer, "users").where("id").eq(1).build())
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
        assertThat(andOr.operator()).isEqualTo(lan.tlab.r4j.sql.ast.common.predicate.logical.LogicalOperator.OR);
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
