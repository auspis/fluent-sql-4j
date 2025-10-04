package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.set.ExceptExpression;
import lan.tlab.r4j.sql.ast.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExceptOracleRenderStrategyTest {

    private ExceptRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = ExceptRenderStrategy.oracle();
        sqlRenderer = SqlRendererFactory.oracle();
    }

    @Test
    void except() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer", "email")))
                        .build())
                .from(From.fromTable("Customer"))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Account", "email")))
                        .build())
                .from(From.fromTable("Account"))
                .build();

        ExceptExpression except = ExceptExpression.except(select1, select2);
        String sql = strategy.render(except, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
			((SELECT \"Customer\".\"email\" FROM \"Customer\") \
			MINUS \
			(SELECT \"Account\".\"email\" FROM \"Account\"))\
			""");
    }

    @Test
    void exceptAll() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer", "email")))
                        .build())
                .from(From.fromTable("Customer"))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Account", "email")))
                        .build())
                .from(From.fromTable("Account"))
                .build();

        ExceptExpression except = ExceptExpression.exceptAll(select1, select2);
        String sql = strategy.render(except, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
			((SELECT \"Customer\".\"email\" FROM \"Customer\") \
			MINUS \
			(SELECT \"Account\".\"email\" FROM \"Account\"))\
			""");
    }
}
