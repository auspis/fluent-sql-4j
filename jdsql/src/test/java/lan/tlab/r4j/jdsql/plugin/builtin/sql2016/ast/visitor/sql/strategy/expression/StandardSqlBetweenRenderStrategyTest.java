package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.predicate.Between;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlBetweenRenderStrategyTest {

    private StandardSqlBetweenRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlBetweenRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void numbers() {
        Between exp = new Between(ColumnReference.of("Customer", "score"), Literal.of(400), Literal.of(405.02));

        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(\"Customer\".\"score\" BETWEEN 400 AND 405.02)");
    }

    @Test
    void dates() {
        Between exp = new Between(
                ColumnReference.of("Customer", "birthdate"),
                Literal.of(LocalDate.of(2000, 2, 3)),
                Literal.of(LocalDate.of(2000, 2, 4)));

        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(\"Customer\".\"birthdate\" BETWEEN '2000-02-03' AND '2000-02-04')");
    }
}
