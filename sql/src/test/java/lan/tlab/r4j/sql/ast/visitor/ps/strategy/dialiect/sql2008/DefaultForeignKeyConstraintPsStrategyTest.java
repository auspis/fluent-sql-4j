package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.item.ddl.Constraint.ForeignKeyConstraint;
import lan.tlab.r4j.sql.ast.expression.item.ddl.ReferencesItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ForeignKeyConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class DefaultForeignKeyConstraintPsStrategyTest {

    private final ForeignKeyConstraintPsStrategy strategy = new DefaultForeignKeyConstraintPsStrategy();
    private final PreparedStatementVisitor visitor = new PreparedStatementVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void foreignKeyConstraintGeneratesCorrectSql() {
        ReferencesItem references = new ReferencesItem("users", "id");
        ForeignKeyConstraint constraint = new ForeignKeyConstraint(List.of("user_id"), references);

        PsDto result = strategy.handle(constraint, visitor, ctx);

        assertThat(result.sql()).contains("FOREIGN KEY");
        assertThat(result.sql()).contains("user_id");
        assertThat(result.sql()).contains("REFERENCES");
        assertThat(result.sql()).contains("users");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void foreignKeyConstraintMultipleColumns() {
        ReferencesItem references = new ReferencesItem("addresses", "country_id", "city_id");
        ForeignKeyConstraint constraint = new ForeignKeyConstraint(List.of("country_id", "city_id"), references);

        PsDto result = strategy.handle(constraint, visitor, ctx);

        assertThat(result.sql()).contains("FOREIGN KEY");
        assertThat(result.sql()).contains("country_id");
        assertThat(result.sql()).contains("city_id");
        assertThat(result.sql()).contains("REFERENCES");
        assertThat(result.sql()).contains("addresses");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleInstancesSameResult() {
        ReferencesItem references1 = new ReferencesItem("users", "id");
        ReferencesItem references2 = new ReferencesItem("users", "id");
        ForeignKeyConstraint constraint1 = new ForeignKeyConstraint(List.of("user_id"), references1);
        ForeignKeyConstraint constraint2 = new ForeignKeyConstraint(List.of("user_id"), references2);

        PsDto result1 = strategy.handle(constraint1, visitor, ctx);
        PsDto result2 = strategy.handle(constraint2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
