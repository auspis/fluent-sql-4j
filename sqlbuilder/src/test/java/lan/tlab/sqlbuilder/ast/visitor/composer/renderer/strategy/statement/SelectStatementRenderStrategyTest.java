package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.conditional.having.Having;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.Sorting;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.CountStar;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectStatementRenderStrategyTest {

    private SelectStatementRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new SelectStatementRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void star() {
        SelectStatement statement = SelectStatement.builder()
                .from(From.builder().source(new Table("users")).build())
                .build();

        String sql = strategy.render(statement, renderer);
        assertThat(sql).isEqualTo("SELECT * FROM \"users\"");
    }

    @Test
    void alias() {
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("u", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("u", "name"))))
                .from(From.fromTable("users", "u"))
                .build();

        String sql = strategy.render(statement, renderer);
        assertThat(sql).isEqualTo("""
    		SELECT \"u\".\"id\", \"u\".\"name\" FROM \"users\" AS u\
    		""");
    }

    @Test
    void where() {
        SelectStatement statement = SelectStatement.builder()
                .from(From.fromTable("products"))
                .where(Where.andOf(
                        Comparison.gt(ColumnReference.of("products", "price"), Literal.of(50)),
                        Comparison.eq(ColumnReference.of("products", "category"), Literal.of("electronics"))))
                .build();

        String sql = strategy.render(statement, renderer);
        assertThat(sql)
                .isEqualTo(
                        """
			SELECT * FROM \"products\" \
			WHERE (\"products\".\"price\" > 50) \
			AND (\"products\".\"category\" = 'electronics')\
			""");
    }

    @Test
    void groupByAndHaving() {
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("employees", "department")),
                        new ScalarExpressionProjection(new CountStar())))
                .from(From.fromTable("employees"))
                .groupBy(GroupBy.of(ColumnReference.of("employees", "department")))
                .having(Having.of(Comparison.gt(new CountStar(), Literal.of(10))))
                .build();

        String sql = strategy.render(statement, renderer);
        assertThat(sql)
                .isEqualTo(
                        """
			SELECT \"employees\".\"department\", COUNT(*) \
			FROM \"employees\" \
			GROUP BY \"employees\".\"department\" \
			HAVING COUNT(*) > 10\
			""");
    }

    @Test
    void orderBy() {
        SelectStatement statement = SelectStatement.builder()
                .from(From.builder().source(new Table("orders")).build())
                .orderBy(OrderBy.of(Sorting.desc(ColumnReference.of("orders", "orderDate"))))
                .build();

        String sql = strategy.render(statement, renderer);
        assertThat(sql)
                .isEqualTo("""
			SELECT * \
			FROM \"orders\" \
			ORDER BY \"orders\".\"orderDate\" DESC\
			""");
    }
}
