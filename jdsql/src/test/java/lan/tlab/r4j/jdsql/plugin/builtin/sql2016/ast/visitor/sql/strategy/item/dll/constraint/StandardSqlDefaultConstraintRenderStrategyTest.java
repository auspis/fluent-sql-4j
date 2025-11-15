package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint.StandardSqlDefaultConstraintRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlDefaultConstraintRenderStrategyTest {

    private StandardSqlDefaultConstraintRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlDefaultConstraintRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void string() {
        DefaultConstraintDefinition constraint = new DefaultConstraintDefinition(Literal.of("def-val"));
        String sql = strategy.render(constraint, renderer, new AstContext());
        assertThat(sql).isEqualTo("DEFAULT 'def-val'");
    }

    @Test
    void number() {
        DefaultConstraintDefinition constraint = new DefaultConstraintDefinition(Literal.of(42));
        String sql = strategy.render(constraint, renderer, new AstContext());
        assertThat(sql).isEqualTo("DEFAULT 42");
    }
}
