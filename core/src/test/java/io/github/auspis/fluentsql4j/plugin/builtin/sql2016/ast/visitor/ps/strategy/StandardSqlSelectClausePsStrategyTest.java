package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.Expression;
import io.github.auspis.fluentsql4j.ast.core.identifier.Alias;
import io.github.auspis.fluentsql4j.ast.dql.clause.Select;
import io.github.auspis.fluentsql4j.ast.dql.projection.Projection;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.SelectClausePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlSelectClausePsStrategy;

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
            return (T) new PreparedStatementSpec(sql, params);
        }
    }

    @Test
    void star() {
        SelectClausePsStrategy strategy = new StandardSqlSelectClausePsStrategy();
        Select select = Select.of();
        PreparedStatementSpec result = strategy.handle(select, null, new AstContext());
        assertThat(result.sql()).isEqualTo("*");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void singleProjection() {
        SelectClausePsStrategy strategy = new StandardSqlSelectClausePsStrategy();
        Projection proj = new StubProjection("col1", List.of(42));
        Select select = Select.of(proj);
        PreparedStatementSpec result = strategy.handle(select, null, new AstContext());
        assertThat(result.sql()).isEqualTo("col1");
        assertThat(result.parameters()).containsExactly(42);
    }

    @Test
    void multipleProjections() {
        SelectClausePsStrategy strategy = new StandardSqlSelectClausePsStrategy();
        Projection p1 = new StubProjection("col1", List.of(1));
        Projection p2 = new StubProjection("col2", List.of(2));
        Select select = Select.of(p1, p2);
        PreparedStatementSpec result = strategy.handle(select, null, new AstContext());
        assertThat(result.sql()).isEqualTo("col1, col2");
        assertThat(result.parameters()).containsExactly(1, 2);
    }
}
