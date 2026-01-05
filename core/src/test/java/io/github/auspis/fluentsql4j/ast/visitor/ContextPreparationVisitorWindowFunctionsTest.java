package io.github.auspis.fluentsql4j.ast.visitor;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.window.DenseRank;
import io.github.auspis.fluentsql4j.ast.core.expression.window.Lag;
import io.github.auspis.fluentsql4j.ast.core.expression.window.Lead;
import io.github.auspis.fluentsql4j.ast.core.expression.window.Ntile;
import io.github.auspis.fluentsql4j.ast.core.expression.window.OverClause;
import io.github.auspis.fluentsql4j.ast.dql.clause.From;
import io.github.auspis.fluentsql4j.ast.dql.clause.Select;
import io.github.auspis.fluentsql4j.ast.dql.clause.Sorting;
import io.github.auspis.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextPreparationVisitorWindowFunctionsTest {

    private ContextPreparationVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new ContextPreparationVisitor();
    }

    @Test
    void denseRankWithOverClauseDetectsWindowFeature() {
        OverClause over = OverClause.builder()
                .partitionBy(ColumnReference.of("users", "department"))
                .orderBy(Sorting.desc(ColumnReference.of("users", "salary")))
                .build();

        DenseRank denseRank = new DenseRank(over);

        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(denseRank)))
                .from(From.fromTable("users"))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.WINDOW_FUNCTION)).isTrue();
    }

    @Test
    void lagWithOverClauseDetectsWindowFeature() {
        OverClause over = OverClause.builder()
                .orderBy(Sorting.asc(ColumnReference.of("users", "hire_date")))
                .build();

        Lag lag = new Lag(ColumnReference.of("users", "salary"), 1, null, over);

        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(lag)))
                .from(From.fromTable("users"))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.WINDOW_FUNCTION)).isTrue();
    }

    @Test
    void leadWithoutOverClauseStillDetectsWindowFeature() {
        Lead lead = new Lead(ColumnReference.of("users", "salary"), 1, null, null);

        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(lead)))
                .from(From.fromTable("users"))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.WINDOW_FUNCTION)).isTrue();
    }

    @Test
    void ntileWithOverClauseDetectsWindowFeature() {
        OverClause over = OverClause.builder()
                .orderBy(Sorting.desc(ColumnReference.of("users", "salary")))
                .build();

        Ntile ntile = new Ntile(4, over);

        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ntile)))
                .from(From.fromTable("users"))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.hasFeature(AstContext.Feature.WINDOW_FUNCTION)).isTrue();
    }
}
