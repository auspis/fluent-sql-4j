package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonQuery;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.WrapperBehavior;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultJsonQueryPsStrategyTest {

    @Test
    void withBasicArguments() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        DefaultJsonQueryPsStrategy strategy = new DefaultJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("products", "data"), Literal.of("$.tags"));

        PsDto result = strategy.handle(jsonQuery, renderer, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_QUERY(\"data\", ?)");
        assertThat(result.parameters()).containsExactly("$.tags");
    }

    @Test
    void withAllOptions() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        DefaultJsonQueryPsStrategy strategy = new DefaultJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("products", "data"),
                Literal.of("$.tags"),
                "JSON",
                WrapperBehavior.WITH_WRAPPER,
                BehaviorKind.DEFAULT,
                "EMPTY ARRAY",
                BehaviorKind.NULL);

        PsDto result = strategy.handle(jsonQuery, renderer, new AstContext());

        assertThat(result.sql())
                .isEqualTo("JSON_QUERY(\"data\", ? RETURNING JSON WITH WRAPPER DEFAULT EMPTY ARRAY ON EMPTY)");
        assertThat(result.parameters()).containsExactly("$.tags");
    }
}
