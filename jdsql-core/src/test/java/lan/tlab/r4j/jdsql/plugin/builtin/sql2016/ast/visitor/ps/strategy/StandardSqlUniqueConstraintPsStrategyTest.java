package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UniqueConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlUniqueConstraintPsStrategyTest {

    private final UniqueConstraintPsStrategy strategy = new StandardSqlUniqueConstraintPsStrategy();
    private final PreparedStatementRenderer specFactory = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();

    @Test
    void uniqueConstraintGeneratesCorrectSql() {
        UniqueConstraintDefinition constraint = new UniqueConstraintDefinition("id", "name");

        PreparedStatementSpec result = strategy.handle(constraint, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("UNIQUE (\"id\", \"name\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void uniqueConstraintSingleColumn() {
        UniqueConstraintDefinition constraint = new UniqueConstraintDefinition("email");

        PreparedStatementSpec result = strategy.handle(constraint, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("UNIQUE (\"email\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleInstancesSameResult() {
        UniqueConstraintDefinition constraint1 = new UniqueConstraintDefinition("col1");
        UniqueConstraintDefinition constraint2 = new UniqueConstraintDefinition("col1");

        PreparedStatementSpec result1 = strategy.handle(constraint1, specFactory, ctx);
        PreparedStatementSpec result2 = strategy.handle(constraint2, specFactory, ctx);

        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
