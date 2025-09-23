package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.set.UnionExpression;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultUnionExpressionPsStrategyTest {
    @Test
    void unionOfTwoSimpleSelects() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new lan.tlab.sqlbuilder.ast.expression.item.Table("User")))
                .where(lan.tlab.sqlbuilder.ast.clause.conditional.where.Where.of(
                        lan.tlab.sqlbuilder.ast.expression.bool.Comparison.eq(
                                ColumnReference.of("User", "id"), Literal.of(1))))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new lan.tlab.sqlbuilder.ast.expression.item.Table("User")))
                .where(lan.tlab.sqlbuilder.ast.clause.conditional.where.Where.of(
                        lan.tlab.sqlbuilder.ast.expression.bool.Comparison.eq(
                                ColumnReference.of("User", "id"), Literal.of(2))))
                .build();
        UnionExpression union = UnionExpression.union(select1, select2);
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        DefaultUnionExpressionPsStrategy strategy = new DefaultUnionExpressionPsStrategy();
        PsDto result = strategy.handle(union, visitor, new AstContext());
        Assertions.assertThat(result.sql())
                .isEqualTo(
                        "((SELECT \"id\" FROM \"User\" WHERE \"id\" = ?) UNION (SELECT \"id\" FROM \"User\" WHERE \"id\" = ?))");
        Assertions.assertThat(result.parameters()).containsExactly(1, 2);
    }
}
