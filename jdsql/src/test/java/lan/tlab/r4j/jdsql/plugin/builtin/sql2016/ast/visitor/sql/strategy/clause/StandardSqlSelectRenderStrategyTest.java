package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.jdsql.ast.common.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.projection.AggregateCallProjection;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlSelectRenderStrategyTest {

    private StandardSqlSelectRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlSelectRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void ok() {
        Select select = Select.of(new ScalarExpressionProjection(ColumnReference.of("Customer", "name")));
        String sql = strategy.render(select, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SELECT \"Customer\".\"name\"");
    }

    @Test
    void star() {
        Select select = new Select();
        String sql = strategy.render(select, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SELECT *");
    }

    @Test
    void scalarExpression() {
        Select select = Select.of(
                new ScalarExpressionProjection(Literal.of(1)),
                new ScalarExpressionProjection(Literal.of("hi"), new Alias("salutation")),
                new ScalarExpressionProjection(ColumnReference.of("Customer", "name")),
                new ScalarExpressionProjection(
                        ArithmeticExpression.modulo(ColumnReference.of("Customer", "score"), Literal.of(2)),
                        new Alias("modScore")));
        String sql = strategy.render(select, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "SELECT 1, 'hi' AS salutation, \"Customer\".\"name\", (\"Customer\".\"score\" % 2) AS modScore");
    }

    @Test
    void aggregationFunction() {
        Select select = Select.of(
                new AggregateCallProjection(AggregateCall.sum(ColumnReference.of("Customer", "score"))),
                new AggregateCallProjection(
                        AggregateCall.sum(ColumnReference.of("Customer", "amount")), new Alias("debt")));
        String sql = strategy.render(select, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SELECT SUM(\"Customer\".\"score\"), SUM(\"Customer\".\"amount\") AS debt");
    }
}
