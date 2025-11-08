package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.Expression;
import lan.tlab.r4j.sql.ast.common.identifier.Alias;
import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.dql.projection.Projection;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SelectClausePsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlSelectClausePsStrategyTest {
    static class StubProjection extends Projection {
        private final String sql;
        private final List<Object> params;

        StubProjection(String sql, List<Object> params) {
            super((Expression) null, (Alias) null);
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
        SelectClausePsStrategy strategy = new StandardSqlSelectClausePsStrategy();
        Select select = Select.of();
        PsDto result = strategy.handle(select, null, new AstContext());
        assertThat(result.sql()).isEqualTo("*");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void singleProjection() {
        SelectClausePsStrategy strategy = new StandardSqlSelectClausePsStrategy();
        Projection proj = new StubProjection("col1", List.of(42));
        Select select = Select.of(proj);
        PsDto result = strategy.handle(select, null, new AstContext());
        assertThat(result.sql()).isEqualTo("col1");
        assertThat(result.parameters()).containsExactly(42);
    }

    @Test
    void multipleProjections() {
        SelectClausePsStrategy strategy = new StandardSqlSelectClausePsStrategy();
        Projection p1 = new StubProjection("col1", List.of(1));
        Projection p2 = new StubProjection("col2", List.of(2));
        Select select = Select.of(p1, p2);
        PsDto result = strategy.handle(select, null, new AstContext());
        assertThat(result.sql()).isEqualTo("col1, col2");
        assertThat(result.parameters()).containsExactly(1, 2);
    }
}
