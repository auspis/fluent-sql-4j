package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import org.junit.jupiter.api.Test;

class StandardSqlNullPredicatePsStrategyTest {

    @Test
    void handleNullPredicate() {
        var expression = new NullPredicate();

        var strategy = new StandardSqlNullPredicatePsStrategy();
        var visitor = new PreparedStatementRenderer();
        var result = strategy.handle(expression, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("NULL");
        assertThat(result.parameters()).isEmpty();
    }
}
