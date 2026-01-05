package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.BehaviorKind;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.JsonExists;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlJsonExistsPsStrategy;

class StandardSqlJsonExistsPsStrategyTest {

    @Test
    void withBasicArguments() {
        AstToPreparedStatementSpecVisitor specFactory =
                AstToPreparedStatementSpecVisitor.builder().build();
        StandardSqlJsonExistsPsStrategy strategy = new StandardSqlJsonExistsPsStrategy();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"));

        PreparedStatementSpec result = strategy.handle(jsonExists, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_EXISTS(\"data\", ?)");
        assertThat(result.parameters()).containsExactly("$.price");
    }

    @Test
    void withOnErrorBehavior() {
        AstToPreparedStatementSpecVisitor specFactory =
                AstToPreparedStatementSpecVisitor.builder().build();
        StandardSqlJsonExistsPsStrategy strategy = new StandardSqlJsonExistsPsStrategy();
        JsonExists jsonExists =
                new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"), BehaviorKind.ERROR);

        PreparedStatementSpec result = strategy.handle(jsonExists, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_EXISTS(\"data\", ? ERROR ON ERROR)");
        assertThat(result.parameters()).containsExactly("$.price");
    }
}
