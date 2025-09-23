package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.from.source.FromSubquery;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import org.junit.jupiter.api.Test;

class DefaultFromSubqueryPsStrategyTest {

    @Test
    void handleFromSubqueryWithAlias() {
        var subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(lan.tlab.sqlbuilder.ast.clause.from.From.of(new Table("User")))
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
                .from(lan.tlab.sqlbuilder.ast.clause.from.From.of(new Table("User")))
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
                .from(lan.tlab.sqlbuilder.ast.clause.from.From.of(new Table("User")))
                .where(lan.tlab.sqlbuilder.ast.clause.conditional.where.Where.of(
                        lan.tlab.sqlbuilder.ast.expression.bool.Comparison.eq(
                                ColumnReference.of("User", "name"),
                                lan.tlab.sqlbuilder.ast.expression.scalar.Literal.of("John"))))
                .build();

        var fromSubquery = FromSubquery.of(subquery, new As("sub"));

        var strategy = new DefaultFromSubqueryPsStrategy();
        var visitor = new PreparedStatementVisitor();
        var result = strategy.handle(fromSubquery, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("(SELECT \"id\" FROM \"User\" WHERE \"name\" = ?) AS \"sub\"");
        assertThat(result.parameters()).containsExactly("John");
    }
}
