package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ArithmeticExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.sql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.dql.clause.From;
import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.dql.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlUnaryArithmeticExpressionRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlUnaryArithmeticExpressionRenderStrategyTest {

    private StandardSqlUnaryArithmeticExpressionRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlUnaryArithmeticExpressionRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void negation() {
        UnaryArithmeticExpression expression = ArithmeticExpression.negation(Literal.of(10));
        String sql = strategy.render(expression, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(-10)");
    }

    @Test
    void negationSubquery() {
        ScalarSubquery subquery = ScalarSubquery.builder()
                .tableExpression(SelectStatement.builder()
                        .select(Select.of(
                                new AggregateCallProjection(AggregateCall.max(ColumnReference.of("Risk", "value")))))
                        .from(From.fromTable("Risk"))
                        .build())
                .build();

        UnaryArithmeticExpression expression = ArithmeticExpression.negation(subquery);
        String sql = strategy.render(expression, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(-(SELECT MAX(\"Risk\".\"value\") FROM \"Risk\"))");
    }
}
