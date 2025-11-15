package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.set.ExceptExpression;
import lan.tlab.r4j.sql.ast.dql.clause.From;
import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExceptRenderStrategy;
import lan.tlab.r4j.sql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlExceptRenderStrategyTest {

    private ExceptRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlExceptRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
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
			EXCEPT \
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
			EXCEPT ALL \
			(SELECT \"Account\".\"email\" FROM \"Account\"))\
			""");
    }
}
