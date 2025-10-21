package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ReferencesItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ForeignKeyConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class DefaultForeignKeyConstraintPsStrategyTest {

    private final ForeignKeyConstraintPsStrategy strategy = new DefaultForeignKeyConstraintPsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();

    @Test
    void foreignKeyConstraintGeneratesCorrectSql() {
        ReferencesItem references = new ReferencesItem("users", "id");
        ForeignKeyConstraintDefinition constraint = new ForeignKeyConstraintDefinition(List.of("user_id"), references);

        PsDto result = strategy.handle(constraint, renderer, ctx);

        assertThat(result.sql()).contains("FOREIGN KEY");
        assertThat(result.sql()).contains("user_id");
        assertThat(result.sql()).contains("REFERENCES");
        assertThat(result.sql()).contains("users");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void foreignKeyConstraintMultipleColumns() {
        ReferencesItem references = new ReferencesItem("addresses", "country_id", "city_id");
        ForeignKeyConstraintDefinition constraint =
                new ForeignKeyConstraintDefinition(List.of("country_id", "city_id"), references);

        PsDto result = strategy.handle(constraint, renderer, ctx);

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
        ForeignKeyConstraintDefinition constraint1 =
                new ForeignKeyConstraintDefinition(List.of("user_id"), references1);
        ForeignKeyConstraintDefinition constraint2 =
                new ForeignKeyConstraintDefinition(List.of("user_id"), references2);

        PsDto result1 = strategy.handle(constraint1, renderer, ctx);
        PsDto result2 = strategy.handle(constraint2, renderer, ctx);

        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
