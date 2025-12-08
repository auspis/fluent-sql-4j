package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.set.UnionExpression;
import lan.tlab.r4j.jdsql.ast.dql.clause.From;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UnionExpressionPsStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StandardSqlUnionExpressionPsStrategyTest {
    @Test
    void unionOfTwoSimpleSelects() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier("User")))
                .where(lan.tlab.r4j.jdsql.ast.dql.clause.Where.of(lan.tlab.r4j.jdsql.ast.common.predicate.Comparison.eq(
                        ColumnReference.of("User", "id"), Literal.of(1))))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier("User")))
                .where(lan.tlab.r4j.jdsql.ast.dql.clause.Where.of(lan.tlab.r4j.jdsql.ast.common.predicate.Comparison.eq(
                        ColumnReference.of("User", "id"), Literal.of(2))))
                .build();
        UnionExpression union = UnionExpression.union(select1, select2);
        PreparedStatementRenderer specFactory = new PreparedStatementRenderer();
        UnionExpressionPsStrategy strategy = new StandardSqlUnionExpressionPsStrategy();
        PreparedStatementSpec result = strategy.handle(union, specFactory, new AstContext());
        Assertions.assertThat(result.sql())
                .isEqualTo(
                        "((SELECT \"id\" FROM \"User\" WHERE \"id\" = ?) UNION (SELECT \"id\" FROM \"User\" WHERE \"id\" = ?))");
        Assertions.assertThat(result.parameters()).containsExactly(1, 2);
    }
}
