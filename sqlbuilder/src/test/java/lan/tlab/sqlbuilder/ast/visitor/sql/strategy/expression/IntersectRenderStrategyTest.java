package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.set.IntersectExpression;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntersectRenderStrategyTest {

    private IntersectRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new IntersectRenderStrategy();
        sqlRenderer = SqlRendererFactory.oracle();
    }

    @Test
    void intersect() {
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

        IntersectExpression intersect = IntersectExpression.intersect(select1, select2);
        String sql = strategy.render(intersect, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
			((SELECT \"Customer\".\"email\" FROM \"Customer\") \
			INTERSECT \
			(SELECT \"Account\".\"email\" FROM \"Account\"))\
			""");
    }

    @Test
    void intersectAll() {
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

        IntersectExpression intersect = IntersectExpression.intersectAll(select1, select2);
        String sql = strategy.render(intersect, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
			((SELECT \"Customer\".\"email\" FROM \"Customer\") \
			INTERSECT ALL \
			(SELECT \"Account\".\"email\" FROM \"Account\"))\
			""");
    }
}
