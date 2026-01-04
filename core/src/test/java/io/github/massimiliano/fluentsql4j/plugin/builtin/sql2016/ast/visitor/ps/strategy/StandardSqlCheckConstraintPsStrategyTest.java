package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Between;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Comparison;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.CheckConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlCheckConstraintPsStrategyTest {

    private final CheckConstraintPsStrategy strategy = new StandardSqlCheckConstraintPsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void checkConstraintGeneratesCorrectSql() {
        Comparison expression = Comparison.gt(ColumnReference.of("", "age"), Literal.of(18));
        CheckConstraintDefinition constraint = new CheckConstraintDefinition(expression);

        PreparedStatementSpec result = strategy.handle(constraint, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("CHECK (\"age\" > ?)");
        assertThat(result.parameters()).containsExactly(18);
    }

    @Test
    void checkConstraintWithComplexExpression() {
        Between expression = new Between(ColumnReference.of("", "salary"), Literal.of(1000), Literal.of(10000));
        CheckConstraintDefinition constraint = new CheckConstraintDefinition(expression);

        PreparedStatementSpec result = strategy.handle(constraint, specFactory, ctx);

        assertThat(result.sql()).contains("CHECK");
        assertThat(result.sql()).contains("salary");
        assertThat(result.sql()).contains("BETWEEN");
        assertThat(result.parameters()).containsExactly(1000, 10000);
    }

    @Test
    void multipleInstancesSameResult() {
        Comparison expression1 = Comparison.gt(ColumnReference.of("", "age"), Literal.of(18));
        Comparison expression2 = Comparison.gt(ColumnReference.of("", "age"), Literal.of(18));
        CheckConstraintDefinition constraint1 = new CheckConstraintDefinition(expression1);
        CheckConstraintDefinition constraint2 = new CheckConstraintDefinition(expression2);

        PreparedStatementSpec result1 = strategy.handle(constraint1, specFactory, ctx);
        PreparedStatementSpec result2 = strategy.handle(constraint2, specFactory, ctx);

        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
