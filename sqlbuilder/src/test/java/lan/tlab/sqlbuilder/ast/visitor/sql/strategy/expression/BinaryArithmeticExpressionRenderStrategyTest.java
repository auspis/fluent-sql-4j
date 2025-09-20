package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarSubquery;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BinaryArithmeticExpressionRenderStrategyTest {

    private BinaryArithmeticExpressionRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new BinaryArithmeticExpressionRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void addition_literal() {
        BinaryArithmeticExpression expression = ArithmeticExpression.addition(Literal.of(5), Literal.of(23));
        String sql = strategy.render(expression, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(5 + 23)");
    }

    @Test
    void subtraction_literal() {
        BinaryArithmeticExpression expression = ArithmeticExpression.subtraction(Literal.of(5), Literal.of(23));
        String sql = strategy.render(expression, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(5 - 23)");
    }

    @Test
    void division_literal() {
        BinaryArithmeticExpression expression = ArithmeticExpression.division(Literal.of(10), Literal.of(2));
        String sql = strategy.render(expression, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(10 / 2)");
    }

    @Test
    void modulo_literal() {
        BinaryArithmeticExpression expression = ArithmeticExpression.modulo(Literal.of(10), Literal.of(2));
        String sql = strategy.render(expression, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(10 % 2)");
    }

    @Test
    void subtraction_columnReference() {
        BinaryArithmeticExpressionRenderStrategy strategy = new BinaryArithmeticExpressionRenderStrategy();
        BinaryArithmeticExpression expression =
                ArithmeticExpression.subtraction(ColumnReference.of("Customer", "score"), Literal.of(10));
        String sql = strategy.render(expression, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(\"Customer\".\"score\" - 10)");
    }

    @Test
    void multiplicationSubQuery() {
        ScalarSubquery subquery = ScalarSubquery.builder()
                .tableExpression(SelectStatement.builder()
                        .select(Select.builder()
                                .projection(new AggregationFunctionProjection(
                                        AggregateCall.max(ColumnReference.of("Risk", "value"))))
                                .build())
                        .from(From.fromTable("Risk"))
                        .build())
                .build();
        BinaryArithmeticExpression expression =
                ArithmeticExpression.multiplication(ColumnReference.of("Customer", "score"), subquery);
        String sql = strategy.render(expression, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(\"Customer\".\"score\" * (SELECT MAX(\"Risk\".\"value\") FROM \"Risk\"))");
    }
}
