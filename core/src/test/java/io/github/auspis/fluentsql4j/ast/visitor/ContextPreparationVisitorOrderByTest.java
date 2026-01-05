package io.github.auspis.fluentsql4j.ast.visitor;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.dql.clause.From;
import io.github.auspis.fluentsql4j.ast.dql.clause.OrderBy;
import io.github.auspis.fluentsql4j.ast.dql.clause.Select;
import io.github.auspis.fluentsql4j.ast.dql.clause.Sorting;
import io.github.auspis.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextPreparationVisitorOrderByTest {

    private ContextPreparationVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new ContextPreparationVisitor();
    }

    @Test
    void orderByClauseDoesNotAddFeatures() {
        // SELECT id, name FROM users ORDER BY id ASC
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.fromTable("users"))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("users", "id"))))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.features()).isEmpty();
    }

    @Test
    void multipleOrderByColumnsDoesNotAddFeatures() {
        // SELECT * FROM users ORDER BY name ASC, age DESC, created_at DESC
        SelectStatement statement = SelectStatement.builder()
                .from(From.fromTable("users"))
                .orderBy(OrderBy.of(
                        Sorting.asc(ColumnReference.of("users", "name")),
                        Sorting.desc(ColumnReference.of("users", "age")),
                        Sorting.desc(ColumnReference.of("users", "created_at"))))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.features()).isEmpty();
    }

    @Test
    void orderByAfterWherePreservesWhereFeature() {
        // SELECT * FROM users WHERE active = true ORDER BY name ASC
        SelectStatement statement = SelectStatement.builder()
                .from(From.fromTable("users"))
                .where(io.github.auspis.fluentsql4j.ast.dql.clause.Where.of(
                        io.github.auspis.fluentsql4j.ast.core.predicate.Comparison.eq(
                                ColumnReference.of("users", "active"), Literal.of(true))))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("users", "name"))))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.WHERE)).isTrue();
        assertThat(result.features()).hasSize(1);
    }

    @Test
    void orderByWithExpressionSortingDoesNotAddFeatures() {
        // SELECT id, price FROM products ORDER BY price * quantity DESC
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("products", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("products", "price"))))
                .from(From.fromTable("products"))
                .orderBy(OrderBy.of(Sorting.desc(
                        io.github.auspis.fluentsql4j.ast.core.expression.scalar.ArithmeticExpression.multiplication(
                                ColumnReference.of("products", "price"), ColumnReference.of("products", "quantity")))))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.features()).isEmpty();
    }

    @Test
    void sortingDirectionAscendingDoesNotAddFeatures() {
        Sorting sorting = Sorting.asc(ColumnReference.of("table", "column"));
        AstContext ctx = new AstContext();

        AstContext result = sorting.accept(visitor, ctx);

        assertThat(result.features()).isEmpty();
        assertThat(result).isEqualTo(ctx);
    }

    @Test
    void sortingDirectionDescendingDoesNotAddFeatures() {
        Sorting sorting = Sorting.desc(ColumnReference.of("table", "column"));
        AstContext ctx = new AstContext();

        AstContext result = sorting.accept(visitor, ctx);

        assertThat(result.features()).isEmpty();
        assertThat(result).isEqualTo(ctx);
    }

    @Test
    void sortingDefaultDoesNotAddFeatures() {
        Sorting sorting = Sorting.by(ColumnReference.of("table", "column"));
        AstContext ctx = new AstContext();

        AstContext result = sorting.accept(visitor, ctx);

        assertThat(result.features()).isEmpty();
        assertThat(result).isEqualTo(ctx);
    }

    @Test
    void orderByWithExistingContextPreservesAllFeatures() {
        // SELECT * FROM t1 JOIN t2 ORDER BY t1.id ASC
        io.github.auspis.fluentsql4j.ast.dql.source.join.OnJoin join =
                new io.github.auspis.fluentsql4j.ast.dql.source.join.OnJoin(
                        new io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier("t1"),
                        io.github.auspis.fluentsql4j.ast.dql.source.join.OnJoin.JoinType.INNER,
                        new io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier("t2"),
                        io.github.auspis.fluentsql4j.ast.core.predicate.Comparison.eq(
                                ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));

        SelectStatement statement = SelectStatement.builder()
                .from(From.of(join))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("t1", "id"))))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.JOIN_ON)).isTrue();
        assertThat(result.features()).hasSize(1);
    }
}
