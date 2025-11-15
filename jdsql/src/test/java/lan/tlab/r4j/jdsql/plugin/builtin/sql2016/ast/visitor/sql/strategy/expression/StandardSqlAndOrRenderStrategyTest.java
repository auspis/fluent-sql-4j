package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.common.predicate.logical.AndOr;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlAndOrRenderStrategyTest {

    private StandardSqlAndOrRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlAndOrRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void and() {
        AndOr and = AndOr.and(
                Comparison.eq(ColumnReference.of("Customer", "name"), Literal.of("Jack")),
                Comparison.gt(ColumnReference.of("Customer", "age"), Literal.of(23)));
        String sql = strategy.render(and, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(\"Customer\".\"name\" = 'Jack') AND (\"Customer\".\"age\" > 23)");
    }

    @Test
    void and_empty() {
        AndOr and = AndOr.and();
        String sql = strategy.render(and, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }

    @Test
    void and_onePredicate() {
        AndOr and = AndOr.and(Comparison.gt(ColumnReference.of("Customer", "age"), Literal.of(23)));
        String sql = strategy.render(and, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(\"Customer\".\"age\" > 23)");
    }

    @Test
    void or() {
        AndOr and = AndOr.or(
                Comparison.eq(ColumnReference.of("Customer", "name"), Literal.of("Jack")),
                Comparison.gt(ColumnReference.of("Customer", "age"), Literal.of(23)));
        String sql = strategy.render(and, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(\"Customer\".\"name\" = 'Jack') OR (\"Customer\".\"age\" > 23)");
    }
}
