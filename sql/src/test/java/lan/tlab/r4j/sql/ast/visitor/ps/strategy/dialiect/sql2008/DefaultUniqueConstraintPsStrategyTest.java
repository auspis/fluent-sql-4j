package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.Constraint.UniqueConstraint;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UniqueConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class DefaultUniqueConstraintPsStrategyTest {

    private final UniqueConstraintPsStrategy strategy = new DefaultUniqueConstraintPsStrategy();
    private final PreparedStatementVisitor visitor = new PreparedStatementVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void uniqueConstraintGeneratesCorrectSql() {
        UniqueConstraint constraint = new UniqueConstraint("id", "name");

        PsDto result = strategy.handle(constraint, visitor, ctx);

        assertThat(result.sql()).isEqualTo("UNIQUE (\"id\", \"name\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void uniqueConstraintSingleColumn() {
        UniqueConstraint constraint = new UniqueConstraint("email");

        PsDto result = strategy.handle(constraint, visitor, ctx);

        assertThat(result.sql()).isEqualTo("UNIQUE (\"email\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleInstancesSameResult() {
        UniqueConstraint constraint1 = new UniqueConstraint("col1");
        UniqueConstraint constraint2 = new UniqueConstraint("col1");

        PsDto result1 = strategy.handle(constraint1, visitor, ctx);
        PsDto result2 = strategy.handle(constraint2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
