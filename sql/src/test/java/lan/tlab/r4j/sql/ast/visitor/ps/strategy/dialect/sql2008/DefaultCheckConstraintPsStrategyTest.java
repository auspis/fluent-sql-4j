package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Between;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CheckConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class DefaultCheckConstraintPsStrategyTest {

    private final CheckConstraintPsStrategy strategy = new DefaultCheckConstraintPsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();

    @Test
    void checkConstraintGeneratesCorrectSql() {
        Comparison expression = Comparison.gt(ColumnReference.of("", "age"), Literal.of(18));
        CheckConstraintDefinition constraint = new CheckConstraintDefinition(expression);

        PsDto result = strategy.handle(constraint, renderer, ctx);

        assertThat(result.sql()).isEqualTo("CHECK (\"age\" > 18)");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void checkConstraintWithComplexExpression() {
        Between expression = new Between(ColumnReference.of("", "salary"), Literal.of(1000), Literal.of(10000));
        CheckConstraintDefinition constraint = new CheckConstraintDefinition(expression);

        PsDto result = strategy.handle(constraint, renderer, ctx);

        assertThat(result.sql()).contains("CHECK");
        assertThat(result.sql()).contains("salary");
        assertThat(result.sql()).contains("BETWEEN");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleInstancesSameResult() {
        Comparison expression1 = Comparison.gt(ColumnReference.of("", "age"), Literal.of(18));
        Comparison expression2 = Comparison.gt(ColumnReference.of("", "age"), Literal.of(18));
        CheckConstraintDefinition constraint1 = new CheckConstraintDefinition(expression1);
        CheckConstraintDefinition constraint2 = new CheckConstraintDefinition(expression2);

        PsDto result1 = strategy.handle(constraint1, renderer, ctx);
        PsDto result2 = strategy.handle(constraint2, renderer, ctx);

        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
