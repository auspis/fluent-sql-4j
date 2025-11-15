package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.aggregate.CountStar;
import lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.dql.clause.From;
import lan.tlab.r4j.sql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.sql.ast.dql.clause.Having;
import lan.tlab.r4j.sql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.dql.clause.Sorting;
import lan.tlab.r4j.sql.ast.dql.clause.Where;
import lan.tlab.r4j.sql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlSelectStatementRenderStrategyTest {

    private StandardSqlSelectStatementRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlSelectStatementRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void star() {
        SelectStatement statement = SelectStatement.builder()
                .from(From.of(new TableIdentifier("users")))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
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

        String sql = strategy.render(statement, renderer, new AstContext());
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

        String sql = strategy.render(statement, renderer, new AstContext());
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

        String sql = strategy.render(statement, renderer, new AstContext());
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
                .from(From.of(new TableIdentifier("orders")))
                .orderBy(OrderBy.of(Sorting.desc(ColumnReference.of("orders", "orderDate"))))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo("""
			SELECT * \
			FROM \"orders\" \
			ORDER BY \"orders\".\"orderDate\" DESC\
			""");
    }
}
