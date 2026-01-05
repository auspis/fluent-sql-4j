package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.aggregate.AggregateCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarSubquery;
import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.dql.clause.From;
import io.github.auspis.fluentsql4j.ast.dql.clause.Select;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.dql.projection.AggregateCallProjection;
import io.github.auspis.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ScalarSubqueryPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlScalarSubqueryPsStrategy;

class StandardSqlScalarSubqueryPsStrategyTest {

    private final ScalarSubqueryPsStrategy strategy = new StandardSqlScalarSubqueryPsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void scalarSubqueryWithSimpleSelect() {
        SelectStatement innerSelect = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.of(new TableIdentifier("users")))
                .build();

        ScalarSubquery subquery =
                ScalarSubquery.builder().tableExpression(innerSelect).build();

        PreparedStatementSpec result = strategy.handle(subquery, specFactory, ctx);

        assertThat(result.sql()).startsWith("(");
        assertThat(result.sql()).endsWith(")");
        assertThat(result.sql()).contains("SELECT");
        assertThat(result.sql()).contains("users");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void scalarSubqueryWithParameters() {
        SelectStatement innerSelect = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.countStar())))
                .from(From.of(new TableIdentifier("orders")))
                .where(Where.of(Comparison.eq(ColumnReference.of("orders", "status"), Literal.of("active"))))
                .build();

        ScalarSubquery subquery =
                ScalarSubquery.builder().tableExpression(innerSelect).build();

        PreparedStatementSpec result = strategy.handle(subquery, specFactory, ctx);

        assertThat(result.sql()).startsWith("(");
        assertThat(result.sql()).endsWith(")");
        assertThat(result.sql()).contains("SELECT COUNT(*)");
        assertThat(result.sql()).contains("WHERE");
        assertThat(result.parameters()).containsExactly("active");
    }

    @Test
    void scalarSubqueryWrappingBehavior() {
        SelectStatement innerSelect = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(Literal.of(42))))
                .build();

        ScalarSubquery subquery =
                ScalarSubquery.builder().tableExpression(innerSelect).build();

        PreparedStatementSpec result = strategy.handle(subquery, specFactory, ctx);

        assertThat(result.sql()).contains("(SELECT ?");
        assertThat(result.sql()).endsWith(")");
        assertThat(result.parameters()).containsExactly(42);
    }
}
