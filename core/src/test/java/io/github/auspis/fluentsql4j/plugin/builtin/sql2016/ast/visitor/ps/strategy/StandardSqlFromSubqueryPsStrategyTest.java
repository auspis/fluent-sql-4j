package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.identifier.Alias;
import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.dql.clause.Select;
import io.github.auspis.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.auspis.fluentsql4j.ast.dql.source.FromSubquery;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import org.junit.jupiter.api.Test;

class StandardSqlFromSubqueryPsStrategyTest {

    @Test
    void handleFromSubqueryWithAlias() {
        var subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(io.github.auspis.fluentsql4j.ast.dql.clause.From.of(new TableIdentifier("User")))
                .build();

        var fromSubquery = FromSubquery.of(subquery, "sub");

        var strategy = new StandardSqlFromSubqueryPsStrategy();
        var visitor = new AstToPreparedStatementSpecVisitor();
        var result = strategy.handle(fromSubquery, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("(SELECT \"id\" FROM \"User\") AS \"sub\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleFromSubqueryWithoutAlias() {
        var subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(io.github.auspis.fluentsql4j.ast.dql.clause.From.of(new TableIdentifier("User")))
                .build();

        var fromSubquery = FromSubquery.of(subquery, Alias.nullObject());

        var strategy = new StandardSqlFromSubqueryPsStrategy();
        var visitor = new AstToPreparedStatementSpecVisitor();
        var result = strategy.handle(fromSubquery, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("(SELECT \"id\" FROM \"User\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleFromSubqueryWithParameters() {
        var subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(io.github.auspis.fluentsql4j.ast.dql.clause.From.of(new TableIdentifier("User")))
                .where(io.github.auspis.fluentsql4j.ast.dql.clause.Where.of(
                        io.github.auspis.fluentsql4j.ast.core.predicate.Comparison.eq(
                                ColumnReference.of("User", "name"),
                                io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal.of("John"))))
                .build();

        var fromSubquery = FromSubquery.of(subquery, new Alias("sub"));

        var strategy = new StandardSqlFromSubqueryPsStrategy();
        var visitor = new AstToPreparedStatementSpecVisitor();
        var result = strategy.handle(fromSubquery, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("(SELECT \"id\" FROM \"User\" WHERE \"name\" = ?) AS \"sub\"");
        assertThat(result.parameters()).containsExactly("John");
    }
}
