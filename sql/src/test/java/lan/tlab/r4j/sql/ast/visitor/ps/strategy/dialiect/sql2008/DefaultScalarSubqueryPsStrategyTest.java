package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.item.Table;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ScalarSubqueryPsStrategy;
import org.junit.jupiter.api.Test;

class DefaultScalarSubqueryPsStrategyTest {

    private final ScalarSubqueryPsStrategy strategy = new DefaultScalarSubqueryPsStrategy();
    private final PreparedStatementVisitor visitor = new PreparedStatementVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void scalarSubqueryWithSimpleSelect() {
        SelectStatement innerSelect = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.of(new Table("users")))
                .build();

        ScalarSubquery subquery =
                ScalarSubquery.builder().tableExpression(innerSelect).build();

        PsDto result = strategy.handle(subquery, visitor, ctx);

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
                .from(From.of(new Table("orders")))
                .where(Where.of(Comparison.eq(ColumnReference.of("orders", "status"), Literal.of("active"))))
                .build();

        ScalarSubquery subquery =
                ScalarSubquery.builder().tableExpression(innerSelect).build();

        PsDto result = strategy.handle(subquery, visitor, ctx);

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

        PsDto result = strategy.handle(subquery, visitor, ctx);

        assertThat(result.sql()).contains("(SELECT ?");
        assertThat(result.sql()).endsWith(")");
        assertThat(result.parameters()).containsExactly(42);
    }
}
