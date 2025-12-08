package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.core.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.dql.source.FromSubquery;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import org.junit.jupiter.api.Test;

class StandardSqlFromSubqueryPsStrategyTest {

    @Test
    void handleFromSubqueryWithAlias() {
        var subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(lan.tlab.r4j.jdsql.ast.dql.clause.From.of(new TableIdentifier("User")))
                .build();

        var fromSubquery = FromSubquery.of(subquery, "sub");

        var strategy = new StandardSqlFromSubqueryPsStrategy();
        var visitor = new PreparedStatementRenderer();
        var result = strategy.handle(fromSubquery, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("(SELECT \"id\" FROM \"User\") AS \"sub\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleFromSubqueryWithoutAlias() {
        var subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(lan.tlab.r4j.jdsql.ast.dql.clause.From.of(new TableIdentifier("User")))
                .build();

        var fromSubquery = FromSubquery.of(subquery, Alias.nullObject());

        var strategy = new StandardSqlFromSubqueryPsStrategy();
        var visitor = new PreparedStatementRenderer();
        var result = strategy.handle(fromSubquery, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("(SELECT \"id\" FROM \"User\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleFromSubqueryWithParameters() {
        var subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(lan.tlab.r4j.jdsql.ast.dql.clause.From.of(new TableIdentifier("User")))
                .where(lan.tlab.r4j.jdsql.ast.dql.clause.Where.of(lan.tlab.r4j.jdsql.ast.core.predicate.Comparison.eq(
                        ColumnReference.of("User", "name"),
                        lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal.of("John"))))
                .build();

        var fromSubquery = FromSubquery.of(subquery, new Alias("sub"));

        var strategy = new StandardSqlFromSubqueryPsStrategy();
        var visitor = new PreparedStatementRenderer();
        var result = strategy.handle(fromSubquery, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("(SELECT \"id\" FROM \"User\" WHERE \"name\" = ?) AS \"sub\"");
        assertThat(result.parameters()).containsExactly("John");
    }
}
