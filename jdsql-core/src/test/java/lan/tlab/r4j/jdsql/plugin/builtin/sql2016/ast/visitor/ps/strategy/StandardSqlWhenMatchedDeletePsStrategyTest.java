package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenMatchedDelete;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlWhenMatchedDeletePsStrategyTest {

    private StandardSqlWhenMatchedDeletePsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlWhenMatchedDeletePsStrategy();
        visitor = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void withoutCondition() {
        WhenMatchedDelete action = new WhenMatchedDelete(null);

        PsDto result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql()).isEqualTo("WHEN MATCHED THEN DELETE");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void withCondition() {
        Comparison condition = Comparison.lt(ColumnReference.of("target", "quantity"), Literal.of(10));
        WhenMatchedDelete action = new WhenMatchedDelete(condition);

        PsDto result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql()).isEqualTo("WHEN MATCHED AND \"quantity\" < ? THEN DELETE");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void complexCondition() {
        Comparison condition = Comparison.eq(ColumnReference.of("target", "status"), Literal.of("inactive"));
        WhenMatchedDelete action = new WhenMatchedDelete(condition);

        PsDto result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql()).isEqualTo("WHEN MATCHED AND \"status\" = ? THEN DELETE");
        assertThat(result.parameters()).containsExactly("inactive");
    }
}
