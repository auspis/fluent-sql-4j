package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.BehaviorKind;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.JsonValue;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.OnEmptyBehavior;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.JsonValuePsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlJsonValuePsStrategy;

class StandardSqlJsonValuePsStrategyTest {

    @Test
    void withBasicArguments() {
        AstToPreparedStatementSpecVisitor specFactory =
                AstToPreparedStatementSpecVisitor.builder().build();
        JsonValuePsStrategy strategy = new StandardSqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"));

        PreparedStatementSpec result = strategy.handle(jsonValue, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_VALUE(\"data\", ?)");
        assertThat(result.parameters()).containsExactly("$.price");
    }

    @Test
    void withAllOptions() {
        AstToPreparedStatementSpecVisitor specFactory =
                AstToPreparedStatementSpecVisitor.builder().build();
        JsonValuePsStrategy strategy = new StandardSqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(
                ColumnReference.of("products", "data"),
                Literal.of("$.price"),
                "DECIMAL(10,2)",
                OnEmptyBehavior.defaultValue("0.0"),
                BehaviorKind.NONE);

        PreparedStatementSpec result = strategy.handle(jsonValue, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_VALUE(\"data\", ? RETURNING DECIMAL(10,2) DEFAULT 0.0 ON EMPTY)");
        assertThat(result.parameters()).containsExactly("$.price");
    }
}
