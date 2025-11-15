package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.OnEmptyBehavior;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.WrapperBehavior;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.JsonQueryPsStrategy;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlJsonQueryPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlJsonQueryPsStrategyTest {

    @Test
    void withBasicArguments() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        JsonQueryPsStrategy strategy = new StandardSqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("products", "data"), Literal.of("$.tags"));

        PsDto result = strategy.handle(jsonQuery, renderer, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_QUERY(\"data\", ?)");
        assertThat(result.parameters()).containsExactly("$.tags");
    }

    @Test
    void withAllOptions() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        JsonQueryPsStrategy strategy = new StandardSqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("products", "data"),
                Literal.of("$.tags"),
                "JSON",
                WrapperBehavior.WITH_WRAPPER,
                OnEmptyBehavior.defaultValue("EMPTY ARRAY"),
                BehaviorKind.NONE);

        PsDto result = strategy.handle(jsonQuery, renderer, new AstContext());

        assertThat(result.sql())
                .isEqualTo("JSON_QUERY(\"data\", ? RETURNING JSON WITH WRAPPER DEFAULT EMPTY ARRAY ON EMPTY)");
        assertThat(result.parameters()).containsExactly("$.tags");
    }
}
