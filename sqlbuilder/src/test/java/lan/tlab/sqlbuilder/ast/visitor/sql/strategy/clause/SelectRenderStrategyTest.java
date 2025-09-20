package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectRenderStrategyTest {

    private SelectRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new SelectRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
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
                new ScalarExpressionProjection(Literal.of("hi"), new As("salutation")),
                new ScalarExpressionProjection(ColumnReference.of("Customer", "name")),
                new ScalarExpressionProjection(
                        ArithmeticExpression.modulo(ColumnReference.of("Customer", "score"), Literal.of(2)),
                        new As("modScore")));
        String sql = strategy.render(select, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "SELECT 1, 'hi' AS salutation, \"Customer\".\"name\", (\"Customer\".\"score\" % 2) AS modScore");
    }

    @Test
    void aggregationFunction() {
        Select select = Select.of(
                new AggregationFunctionProjection(AggregateCall.sum(ColumnReference.of("Customer", "score"))),
                new AggregationFunctionProjection(
                        AggregateCall.sum(ColumnReference.of("Customer", "amount")), new As("debt")));
        String sql = strategy.render(select, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SELECT SUM(\"Customer\".\"score\"), SUM(\"Customer\".\"amount\") AS debt");
    }
}
