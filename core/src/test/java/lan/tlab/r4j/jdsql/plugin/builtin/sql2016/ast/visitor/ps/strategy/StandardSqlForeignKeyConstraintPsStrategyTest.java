package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ReferencesItem;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ForeignKeyConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlForeignKeyConstraintPsStrategyTest {

    private final ForeignKeyConstraintPsStrategy strategy = new StandardSqlForeignKeyConstraintPsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void foreignKeyConstraintGeneratesCorrectSql() {
        ReferencesItem references = new ReferencesItem("users", "id");
        ForeignKeyConstraintDefinition constraint = new ForeignKeyConstraintDefinition(List.of("user_id"), references);

        PreparedStatementSpec result = strategy.handle(constraint, specFactory, ctx);

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

        PreparedStatementSpec result = strategy.handle(constraint, specFactory, ctx);

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

        PreparedStatementSpec result1 = strategy.handle(constraint1, specFactory, ctx);
        PreparedStatementSpec result2 = strategy.handle(constraint2, specFactory, ctx);

        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
