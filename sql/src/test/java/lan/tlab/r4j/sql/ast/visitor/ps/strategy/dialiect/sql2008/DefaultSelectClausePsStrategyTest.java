package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.Projection;
import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.expression.item.As;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultSelectClausePsStrategyTest {
    static class StubProjection extends Projection {
        private final String sql;
        private final List<Object> params;

        StubProjection(String sql, List<Object> params) {
            super((Expression) null, (As) null);
            this.sql = sql;
            this.params = params;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return (T) new PsDto(sql, params);
        }
    }

    @Test
    void star() {
        DefaultSelectClausePsStrategy strategy = new DefaultSelectClausePsStrategy();
        Select select = Select.of();
        PsDto result = strategy.handle(select, null, new AstContext());
        assertThat(result.sql()).isEqualTo("*");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void singleProjection() {
        DefaultSelectClausePsStrategy strategy = new DefaultSelectClausePsStrategy();
        Projection proj = new StubProjection("col1", List.of(42));
        Select select = Select.of(proj);
        PsDto result = strategy.handle(select, null, new AstContext());
        assertThat(result.sql()).isEqualTo("col1");
        assertThat(result.parameters()).containsExactly(42);
    }

    @Test
    void multipleProjections() {
        DefaultSelectClausePsStrategy strategy = new DefaultSelectClausePsStrategy();
        Projection p1 = new StubProjection("col1", List.of(1));
        Projection p2 = new StubProjection("col2", List.of(2));
        Select select = Select.of(p1, p2);
        PsDto result = strategy.handle(select, null, new AstContext());
        assertThat(result.sql()).isEqualTo("col1, col2");
        assertThat(result.parameters()).containsExactly(1, 2);
    }
}
