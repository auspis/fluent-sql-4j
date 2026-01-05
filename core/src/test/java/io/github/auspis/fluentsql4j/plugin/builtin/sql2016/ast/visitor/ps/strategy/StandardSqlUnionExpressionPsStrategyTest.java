package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.set.UnionExpression;
import io.github.auspis.fluentsql4j.ast.dql.clause.From;
import io.github.auspis.fluentsql4j.ast.dql.clause.Select;
import io.github.auspis.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UnionExpressionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUnionExpressionPsStrategy;

class StandardSqlUnionExpressionPsStrategyTest {
    @Test
    void unionOfTwoSimpleSelects() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier("User")))
                .where(io.github.auspis.fluentsql4j.ast.dql.clause.Where.of(
                        io.github.auspis.fluentsql4j.ast.core.predicate.Comparison.eq(
                                ColumnReference.of("User", "id"), Literal.of(1))))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier("User")))
                .where(io.github.auspis.fluentsql4j.ast.dql.clause.Where.of(
                        io.github.auspis.fluentsql4j.ast.core.predicate.Comparison.eq(
                                ColumnReference.of("User", "id"), Literal.of(2))))
                .build();
        UnionExpression union = UnionExpression.union(select1, select2);
        AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
        UnionExpressionPsStrategy strategy = new StandardSqlUnionExpressionPsStrategy();
        PreparedStatementSpec result = strategy.handle(union, specFactory, new AstContext());
        Assertions.assertThat(result.sql())
                .isEqualTo(
                        "((SELECT \"id\" FROM \"User\" WHERE \"id\" = ?) UNION (SELECT \"id\" FROM \"User\" WHERE \"id\" = ?))");
        Assertions.assertThat(result.parameters()).containsExactly(1, 2);
    }
}
