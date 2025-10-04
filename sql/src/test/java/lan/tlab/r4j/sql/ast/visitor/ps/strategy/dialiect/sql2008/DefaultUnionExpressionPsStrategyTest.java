package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.set.UnionExpression;
import lan.tlab.r4j.sql.ast.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultUnionExpressionPsStrategyTest {
    @Test
    void unionOfTwoSimpleSelects() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new lan.tlab.r4j.sql.ast.expression.item.Table("User")))
                .where(lan.tlab.r4j.sql.ast.clause.conditional.where.Where.of(
                        lan.tlab.r4j.sql.ast.expression.bool.Comparison.eq(
                                ColumnReference.of("User", "id"), Literal.of(1))))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new lan.tlab.r4j.sql.ast.expression.item.Table("User")))
                .where(lan.tlab.r4j.sql.ast.clause.conditional.where.Where.of(
                        lan.tlab.r4j.sql.ast.expression.bool.Comparison.eq(
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
