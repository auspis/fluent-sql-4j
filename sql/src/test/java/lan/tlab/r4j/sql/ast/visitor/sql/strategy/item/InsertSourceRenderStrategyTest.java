package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.set.NullSetExpression;
import lan.tlab.r4j.sql.ast.expression.set.UnionExpression;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertSource;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsertSourceRenderStrategyTest {

    private InsertSourceRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new InsertSourceRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer_current", "id")))
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer_current", "name")))
                        .build())
                .from(From.fromTable("Customer_current"))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer_previous", "id")))
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer_previous", "name")))
                        .build())
                .from(From.fromTable("Customer_previous"))
                .build();
        InsertSource item = new InsertSource(UnionExpression.union(select1, select2));

        String sql = strategy.render(item, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
			(\
			(SELECT \"Customer_current\".\"id\", \"Customer_current\".\"name\" FROM \"Customer_current\") \
			UNION \
			(SELECT \"Customer_previous\".\"id\", \"Customer_previous\".\"name\" FROM \"Customer_previous\")\
			)\
			""");
    }

    @Test
    void empty() {
        InsertSource item = new InsertSource(new NullSetExpression());
        String sql = strategy.render(item, renderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }
}
