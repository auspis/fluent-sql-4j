package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.aggregate.CountStar;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Comparison;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.From;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.GroupBy;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Having;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.OrderBy;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Select;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Sorting;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Where;
import io.github.massimiliano.fluentsql4j.ast.dql.projection.AggregateCallProjection;
import io.github.massimiliano.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.massimiliano.fluentsql4j.ast.dql.source.join.OnJoin;
import io.github.massimiliano.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.Test;

class StandardSqlSelectStatementPsStrategyTest {

    private final StandardSqlSelectStatementPsStrategy strategy = new StandardSqlSelectStatementPsStrategy();
    private final AstToPreparedStatementSpecVisitor astToPsSpecVisitor = new AstToPreparedStatementSpecVisitor();
    private final AstContext ctx = new AstContext();
    private final PreparedStatementSpecFactory specFactory = new PreparedStatementSpecFactory(
            AstToPreparedStatementSpecVisitor.builder().build());

    @Test
    void star() {
        SelectStatement statement = SelectStatement.builder()
                .from(From.of(new TableIdentifier("users")))
                .build();

        PreparedStatementSpec spec = strategy.handle(statement, astToPsSpecVisitor, ctx);
        assertThat(spec.sql()).isEqualTo("SELECT * FROM \"users\"");
        assertThat(spec.parameters()).isEmpty();
    }

    @Test
    void alias() {
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("u", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("u", "name"))))
                .from(From.fromTable("users", "u"))
                .build();

        PreparedStatementSpec spec = strategy.handle(statement, astToPsSpecVisitor, ctx);
        assertThat(spec.sql()).isEqualTo("""
            SELECT \"id\", \"name\" FROM \"users\" AS u\
            """);
        assertThat(spec.parameters()).isEmpty();
    }

    @Test
    void where() {
        SelectStatement statement = SelectStatement.builder()
                .from(From.fromTable("products"))
                .where(Where.andOf(
                        Comparison.gt(ColumnReference.of("products", "price"), Literal.of(50)),
                        Comparison.eq(ColumnReference.of("products", "category"), Literal.of("electronics"))))
                .build();

        PreparedStatementSpec spec = strategy.handle(statement, astToPsSpecVisitor, ctx);
        assertThat(spec.sql()).isEqualTo("""
            SELECT * FROM \"products\" \
            WHERE (\"price\" > ?) \
            AND (\"category\" = ?)\
            """);
        assertThat(spec.parameters()).containsExactly(50, "electronics");
    }

    @Test
    void groupByAndHaving() {
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("employees", "department")),
                        new AggregateCallProjection(new CountStar())))
                .from(From.fromTable("employees"))
                .groupBy(GroupBy.of(ColumnReference.of("employees", "department")))
                .having(Having.of(Comparison.gt(new CountStar(), Literal.of(10))))
                .build();

        PreparedStatementSpec spec = strategy.handle(statement, astToPsSpecVisitor, ctx);
        assertThat(spec.sql()).isEqualTo("""
            SELECT \"department\", COUNT(*) \
            FROM \"employees\" \
            GROUP BY \"department\" \
            HAVING COUNT(*) > ?\
            """);
        assertThat(spec.parameters()).containsExactly(10);
    }

    @Test
    void orderBy() {
        SelectStatement statement = SelectStatement.builder()
                .from(From.of(new TableIdentifier("orders")))
                .orderBy(OrderBy.of(Sorting.desc(ColumnReference.of("orders", "orderDate"))))
                .build();

        PreparedStatementSpec spec = strategy.handle(statement, astToPsSpecVisitor, ctx);
        assertThat(spec.sql()).isEqualTo("""
            SELECT * \
            FROM \"orders\" \
            ORDER BY \"orderDate\" DESC\
            """);
        assertThat(spec.parameters()).isEmpty();
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

        // Use PreparedStatementSpecFactory to trigger context-aware rendering with ContextPreparationVisitor
        PreparedStatementSpec spec = specFactory.create(statement);
        assertThat(spec.sql()).isEqualTo("""
            SELECT \"t1\".\"id\", \"t2\".\"name\" \
            FROM \"t1\" INNER JOIN \"t2\" \
            ON \"t1\".\"id\" = \"t2\".\"t1_id\"\
            """);
        assertThat(spec.parameters()).isEmpty();
    }
}
