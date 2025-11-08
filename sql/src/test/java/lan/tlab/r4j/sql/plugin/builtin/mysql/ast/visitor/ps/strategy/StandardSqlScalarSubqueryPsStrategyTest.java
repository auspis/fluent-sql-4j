package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.sql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.dql.clause.From;
import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.dql.clause.Where;
import lan.tlab.r4j.sql.ast.dql.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ScalarSubqueryPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlScalarSubqueryPsStrategyTest {

    private final ScalarSubqueryPsStrategy strategy = new StandardSqlScalarSubqueryPsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();

    @Test
    void scalarSubqueryWithSimpleSelect() {
        SelectStatement innerSelect = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.of(new TableIdentifier("users")))
                .build();

        ScalarSubquery subquery =
                ScalarSubquery.builder().tableExpression(innerSelect).build();

        PsDto result = strategy.handle(subquery, renderer, ctx);

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

        PsDto result = strategy.handle(subquery, renderer, ctx);

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

        PsDto result = strategy.handle(subquery, renderer, ctx);

        assertThat(result.sql()).contains("(SELECT ?");
        assertThat(result.sql()).endsWith(")");
        assertThat(result.parameters()).containsExactly(42);
    }
}
