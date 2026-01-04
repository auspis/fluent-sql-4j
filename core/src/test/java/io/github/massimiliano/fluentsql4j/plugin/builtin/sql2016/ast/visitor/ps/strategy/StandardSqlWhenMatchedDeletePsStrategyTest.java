package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Comparison;
import io.github.massimiliano.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedDelete;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlWhenMatchedDeletePsStrategyTest {

    private StandardSqlWhenMatchedDeletePsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlWhenMatchedDeletePsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void withoutCondition() {
        WhenMatchedDelete action = new WhenMatchedDelete(null);

        PreparedStatementSpec result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql()).isEqualTo("WHEN MATCHED THEN DELETE");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void withCondition() {
        Comparison condition = Comparison.lt(ColumnReference.of("target", "quantity"), Literal.of(10));
        WhenMatchedDelete action = new WhenMatchedDelete(condition);

        PreparedStatementSpec result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql()).isEqualTo("WHEN MATCHED AND \"quantity\" < ? THEN DELETE");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void complexCondition() {
        Comparison condition = Comparison.eq(ColumnReference.of("target", "status"), Literal.of("inactive"));
        WhenMatchedDelete action = new WhenMatchedDelete(condition);

        PreparedStatementSpec result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql()).isEqualTo("WHEN MATCHED AND \"status\" = ? THEN DELETE");
        assertThat(result.parameters()).containsExactly("inactive");
    }
}
