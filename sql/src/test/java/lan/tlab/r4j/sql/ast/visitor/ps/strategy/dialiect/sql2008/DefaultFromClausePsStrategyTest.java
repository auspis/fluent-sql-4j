package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultFromClausePsStrategyTest {

    private DefaultFromClausePsStrategy strategy;
    private PreparedStatementVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new DefaultFromClausePsStrategy();
        visitor = new PreparedStatementVisitor();
        ctx = new AstContext();
    }

    @Test
    void singleTable() {
        TableIdentifier table = new TableIdentifier("User");
        From from = From.of(table);

        PsDto result = strategy.handle(from, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void singleTableWithAlias() {
        From from = From.fromTable("User", "u");

        PsDto result = strategy.handle(from, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"User\" AS u");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleTables() {
        TableIdentifier table1 = new TableIdentifier("User");
        TableIdentifier table2 = new TableIdentifier("Order");
        From from = From.of(table1, table2);

        PsDto result = strategy.handle(from, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"User\", \"Order\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleTablesWithAliases() {
        From from = From.of(
                From.fromTable("User", "u").getSources().get(0),
                From.fromTable("Order", "o").getSources().get(0),
                From.fromTable("Product", "p").getSources().get(0));

        PsDto result = strategy.handle(from, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"User\" AS u, \"Order\" AS o, \"Product\" AS p");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void innerJoin() {
        var t1 = new TableIdentifier("User");
        var t2 = new TableIdentifier("Order");
        var join = new lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin(
                t1,
                lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin.JoinType.INNER,
                t2,
                Comparison.eq(ColumnReference.of("User", "id"), ColumnReference.of("Order", "user_id")));
        From from = From.of(join);

        PsDto result = strategy.handle(from, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"User\" INNER JOIN \"Order\" ON \"User\".\"id\" = \"Order\".\"user_id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void leftJoin() {
        var t1 = new TableIdentifier("User");
        var t2 = new TableIdentifier("Profile");
        var join = new lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin(
                t1,
                lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin.JoinType.LEFT,
                t2,
                Comparison.eq(ColumnReference.of("User", "id"), ColumnReference.of("Profile", "user_id")));
        From from = From.of(join);

        PsDto result = strategy.handle(from, visitor, ctx);

        assertThat(result.sql())
                .isEqualTo("\"User\" LEFT JOIN \"Profile\" ON \"User\".\"id\" = \"Profile\".\"user_id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void rightJoin() {
        var t1 = new TableIdentifier("User");
        var t2 = new TableIdentifier("Department");
        var join = new lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin(
                t1,
                lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin.JoinType.RIGHT,
                t2,
                Comparison.eq(ColumnReference.of("User", "dept_id"), ColumnReference.of("Department", "id")));
        From from = From.of(join);

        PsDto result = strategy.handle(from, visitor, ctx);

        assertThat(result.sql())
                .isEqualTo("\"User\" RIGHT JOIN \"Department\" ON \"User\".\"dept_id\" = \"Department\".\"id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void fullJoin() {
        var t1 = new TableIdentifier("User");
        var t2 = new TableIdentifier("Role");
        var join = new lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin(
                t1,
                lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin.JoinType.FULL,
                t2,
                Comparison.eq(ColumnReference.of("User", "role_id"), ColumnReference.of("Role", "id")));
        From from = From.of(join);

        PsDto result = strategy.handle(from, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"User\" FULL JOIN \"Role\" ON \"User\".\"role_id\" = \"Role\".\"id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void crossJoin() {
        var t1 = new TableIdentifier("User");
        var t2 = new TableIdentifier("Settings");
        var join = new lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin(
                t1, lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin.JoinType.CROSS, t2, null);
        From from = From.of(join);

        PsDto result = strategy.handle(from, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"User\" CROSS JOIN \"Settings\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void joinWithParameters() {
        var t1 = new TableIdentifier("User");
        var t2 = new TableIdentifier("Order");
        var join = new lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin(
                t1,
                lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin.JoinType.INNER,
                t2,
                Comparison.gt(ColumnReference.of("Order", "amount"), Literal.of(100)));
        From from = From.of(join);

        PsDto result = strategy.handle(from, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"User\" INNER JOIN \"Order\" ON \"Order\".\"amount\" > ?");
        assertThat(result.parameters()).containsExactly(100);
    }

    @Test
    void multipleJoins() {
        var t1 = new TableIdentifier("User");
        var t2 = new TableIdentifier("Order");
        var t3 = new TableIdentifier("Product");

        var join1 = new lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin(
                t1,
                lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin.JoinType.INNER,
                t2,
                Comparison.eq(ColumnReference.of("User", "id"), ColumnReference.of("Order", "user_id")));

        var join2 = new lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin(
                join1,
                lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin.JoinType.LEFT,
                t3,
                Comparison.eq(ColumnReference.of("Order", "product_id"), ColumnReference.of("Product", "id")));

        From from = From.of(join2);

        PsDto result = strategy.handle(from, visitor, ctx);

        assertThat(result.sql())
                .isEqualTo(
                        "\"User\" INNER JOIN \"Order\" ON \"User\".\"id\" = \"Order\".\"user_id\" LEFT JOIN \"Product\" ON \"Order\".\"product_id\" = \"Product\".\"id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void mixedTablesAndJoins() {
        var table = new TableIdentifier("User");
        var t1 = new TableIdentifier("Order");
        var t2 = new TableIdentifier("Product");
        var join = new lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin(
                t1,
                lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin.JoinType.INNER,
                t2,
                Comparison.eq(ColumnReference.of("Order", "product_id"), ColumnReference.of("Product", "id")));

        From from = From.of(table, join);

        PsDto result = strategy.handle(from, visitor, ctx);

        assertThat(result.sql())
                .isEqualTo(
                        "\"User\", \"Order\" INNER JOIN \"Product\" ON \"Order\".\"product_id\" = \"Product\".\"id\"");
        assertThat(result.parameters()).isEmpty();
    }
}
