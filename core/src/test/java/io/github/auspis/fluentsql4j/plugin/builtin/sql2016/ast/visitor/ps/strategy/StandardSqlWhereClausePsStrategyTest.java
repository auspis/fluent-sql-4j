package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlWhereClausePsStrategyTest {

    private StandardSqlWhereClausePsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlWhereClausePsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void nullPredicate() {
        Where where = Where.nullObject();

        PreparedStatementSpec result = strategy.handle(where, visitor, ctx);

        assertThat(result.sql()).isEmpty();
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void simpleComparison() {
        Where where = Where.of(Comparison.gt(ColumnReference.of("users", "age"), Literal.of(18)));

        PreparedStatementSpec result = strategy.handle(where, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"age\" > ?");
        assertThat(result.parameters()).containsExactly(18);
    }

    @Test
    void multipleConditionsWithAnd() {
        Where where = Where.andOf(
                Comparison.eq(ColumnReference.of("users", "status"), Literal.of("active")),
                Comparison.gt(ColumnReference.of("users", "balance"), Literal.of(100.0)));

        PreparedStatementSpec result = strategy.handle(where, visitor, ctx);

        assertThat(result.sql()).isEqualTo("(\"status\" = ?) AND (\"balance\" > ?)");
        assertThat(result.parameters()).containsExactly("active", 100.0);
    }

    @Test
    void complexConditionWithNestedComparisons() {
        Comparison comp1 = Comparison.eq(ColumnReference.of("users", "status"), Literal.of("active"));
        Comparison comp2 = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(21));
        Where where = Where.andOf(comp1, comp2);

        PreparedStatementSpec result = strategy.handle(where, visitor, ctx);

        assertThat(result.sql()).isEqualTo("(\"status\" = ?) AND (\"age\" > ?)");
        assertThat(result.parameters()).containsExactly("active", 21);
    }

    @Test
    void complexConditionWithColumnReferences() {
        Where where = Where.of(
                Comparison.gt(ColumnReference.of("products", "price"), ColumnReference.of("products", "cost")));

        PreparedStatementSpec result = strategy.handle(where, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"price\" > \"cost\"");
        assertThat(result.parameters()).isEmpty();
    }
}
