package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import org.junit.jupiter.api.Test;

class DefaultNullPredicatePsStrategyTest {

    @Test
    void handleNullPredicate() {
        var expression = new NullPredicate();

        var strategy = new DefaultNullPredicatePsStrategy();
        var visitor = new PreparedStatementVisitor();
        var result = strategy.handle(expression, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("NULL");
        assertThat(result.parameters()).isEmpty();
    }
}
