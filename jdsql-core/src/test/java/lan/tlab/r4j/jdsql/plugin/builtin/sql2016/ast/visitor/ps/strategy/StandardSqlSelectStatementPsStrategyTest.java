package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.CountStar;
import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.dql.clause.From;
import lan.tlab.r4j.jdsql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.jdsql.ast.dql.clause.Having;
import lan.tlab.r4j.jdsql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.clause.Sorting;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.dql.source.join.OnJoin;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import org.junit.jupiter.api.Test;

class StandardSqlSelectStatementPsStrategyTest {

    private final StandardSqlSelectStatementPsStrategy strategy = new StandardSqlSelectStatementPsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();
    private final DialectRenderer dialectRenderer = new DialectRenderer(
            SqlRenderer.builder().build(), PreparedStatementRenderer.builder().build());

    @Test
    void star() {
        SelectStatement statement = SelectStatement.builder()
                .from(From.of(new TableIdentifier("users")))
                .build();

        PsDto psDto = strategy.handle(statement, renderer, ctx);
        assertThat(psDto.sql()).isEqualTo("SELECT * FROM \"users\"");
        assertThat(psDto.parameters()).isEmpty();
    }

    @Test
    void alias() {
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("u", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("u", "name"))))
                .from(From.fromTable("users", "u"))
                .build();

        PsDto psDto = strategy.handle(statement, renderer, ctx);
        assertThat(psDto.sql()).isEqualTo("""
            SELECT \"id\", \"name\" FROM \"users\" AS u\
            """);
        assertThat(psDto.parameters()).isEmpty();
    }

    @Test
    void where() {
        SelectStatement statement = SelectStatement.builder()
                .from(From.fromTable("products"))
                .where(Where.andOf(
                        Comparison.gt(ColumnReference.of("products", "price"), Literal.of(50)),
                        Comparison.eq(ColumnReference.of("products", "category"), Literal.of("electronics"))))
                .build();

        PsDto psDto = strategy.handle(statement, renderer, ctx);
        assertThat(psDto.sql())
                .isEqualTo(
                        """
            SELECT * FROM \"products\" \
            WHERE (\"price\" > ?) \
            AND (\"category\" = ?)\
            """);
        assertThat(psDto.parameters()).containsExactly(50, "electronics");
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

        PsDto psDto = strategy.handle(statement, renderer, ctx);
        assertThat(psDto.sql())
                .isEqualTo(
                        """
            SELECT \"department\", COUNT(*) \
            FROM \"employees\" \
            GROUP BY \"department\" \
            HAVING COUNT(*) > ?\
            """);
        assertThat(psDto.parameters()).containsExactly(10);
    }

    @Test
    void orderBy() {
        SelectStatement statement = SelectStatement.builder()
                .from(From.of(new TableIdentifier("orders")))
                .orderBy(OrderBy.of(Sorting.desc(ColumnReference.of("orders", "orderDate"))))
                .build();

        PsDto psDto = strategy.handle(statement, renderer, ctx);
        assertThat(psDto.sql())
                .isEqualTo(
                        """
            SELECT * \
            FROM \"orders\" \
            ORDER BY \"orderDate\" DESC\
            """);
        assertThat(psDto.parameters()).isEmpty();
    }

    @Test
    void join() {
        var t1 = new TableIdentifier("t1");
        var t2 = new TableIdentifier("t2");
        var join = new OnJoin(
                t1,
                OnJoin.JoinType.INNER,
                t2,
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name"))))
                .from(From.of(join))
                .build();

        // Use DialectRenderer to trigger context-aware rendering with ContextPreparationVisitor
        PsDto psDto = dialectRenderer.renderPreparedStatement(statement);
        assertThat(psDto.sql())
                .isEqualTo(
                        """
            SELECT \"t1\".\"id\", \"t2\".\"name\" \
            FROM \"t1\" INNER JOIN \"t2\" \
            ON \"t1\".\"id\" = \"t2\".\"t1_id\"\
            """);
        assertThat(psDto.parameters()).isEmpty();
    }
}
