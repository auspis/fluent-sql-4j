package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.from.source.FromSubquery;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.identifier.Alias;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import org.junit.jupiter.api.Test;

class DefaultFromSubqueryPsStrategyTest {

    @Test
    void handleFromSubqueryWithAlias() {
        var subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(lan.tlab.r4j.sql.ast.clause.from.From.of(new TableIdentifier("User")))
                .build();

        var fromSubquery = FromSubquery.of(subquery, "sub");

        var strategy = new DefaultFromSubqueryPsStrategy();
        var visitor = new PreparedStatementVisitor();
        var result = strategy.handle(fromSubquery, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("(SELECT \"id\" FROM \"User\") AS \"sub\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleFromSubqueryWithoutAlias() {
        var subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(lan.tlab.r4j.sql.ast.clause.from.From.of(new TableIdentifier("User")))
                .build();

        var fromSubquery = FromSubquery.builder().subquery(subquery).build();

        var strategy = new DefaultFromSubqueryPsStrategy();
        var visitor = new PreparedStatementVisitor();
        var result = strategy.handle(fromSubquery, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("(SELECT \"id\" FROM \"User\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleFromSubqueryWithParameters() {
        var subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(lan.tlab.r4j.sql.ast.clause.from.From.of(new TableIdentifier("User")))
                .where(lan.tlab.r4j.sql.ast.clause.conditional.where.Where.of(
                        lan.tlab.r4j.sql.ast.predicate.Comparison.eq(
                                ColumnReference.of("User", "name"),
                                lan.tlab.r4j.sql.ast.expression.scalar.Literal.of("John"))))
                .build();

        var fromSubquery = FromSubquery.of(subquery, new Alias("sub"));

        var strategy = new DefaultFromSubqueryPsStrategy();
        var visitor = new PreparedStatementVisitor();
        var result = strategy.handle(fromSubquery, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("(SELECT \"id\" FROM \"User\" WHERE \"name\" = ?) AS \"sub\"");
        assertThat(result.parameters()).containsExactly("John");
    }
}
