package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UniqueConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlUniqueConstraintPsStrategyTest {

    private final UniqueConstraintPsStrategy strategy = new StandardSqlUniqueConstraintPsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();

    @Test
    void uniqueConstraintGeneratesCorrectSql() {
        UniqueConstraintDefinition constraint = new UniqueConstraintDefinition("id", "name");

        PsDto result = strategy.handle(constraint, renderer, ctx);

        assertThat(result.sql()).isEqualTo("UNIQUE (\"id\", \"name\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void uniqueConstraintSingleColumn() {
        UniqueConstraintDefinition constraint = new UniqueConstraintDefinition("email");

        PsDto result = strategy.handle(constraint, renderer, ctx);

        assertThat(result.sql()).isEqualTo("UNIQUE (\"email\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleInstancesSameResult() {
        UniqueConstraintDefinition constraint1 = new UniqueConstraintDefinition("col1");
        UniqueConstraintDefinition constraint2 = new UniqueConstraintDefinition("col1");

        PsDto result1 = strategy.handle(constraint1, renderer, ctx);
        PsDto result2 = strategy.handle(constraint2, renderer, ctx);

        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
