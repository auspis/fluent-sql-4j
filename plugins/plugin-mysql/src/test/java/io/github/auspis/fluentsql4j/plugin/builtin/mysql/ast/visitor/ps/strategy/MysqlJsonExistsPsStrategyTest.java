package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.BehaviorKind;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.JsonExists;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.MysqlEscapeStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.MysqlJsonExistsPsStrategy;

class MysqlJsonExistsPsStrategyTest {

    @Test
    void withBasicArguments() {
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonExistsPsStrategy strategy = new MysqlJsonExistsPsStrategy();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("users", "address"), Literal.of("$.city"));

        PreparedStatementSpec result = strategy.handle(jsonExists, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_CONTAINS_PATH(`address`, 'one', ?)");
        assertThat(result.parameters()).containsExactly("$.city");
    }

    @Test
    void withTableQualifiedColumn() {
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonExistsPsStrategy strategy = new MysqlJsonExistsPsStrategy();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("users", "data"), Literal.of("$.email"));

        PreparedStatementSpec result = strategy.handle(jsonExists, specFactory, new AstContext());

        // AstToPreparedStatementSpecVisitor outputs only column name when no table context
        assertThat(result.sql()).isEqualTo("JSON_CONTAINS_PATH(`data`, 'one', ?)");
        assertThat(result.parameters()).containsExactly("$.email");
    }

    @Test
    void withOnErrorBehaviorIsIgnored() {
        // MySQL does not support ON ERROR - it should be ignored
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonExistsPsStrategy strategy = new MysqlJsonExistsPsStrategy();
        JsonExists jsonExists =
                new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"), BehaviorKind.ERROR);

        PreparedStatementSpec result = strategy.handle(jsonExists, specFactory, new AstContext());

        // ON ERROR behavior should be ignored - only JSON_CONTAINS_PATH syntax
        assertThat(result.sql()).isEqualTo("JSON_CONTAINS_PATH(`data`, 'one', ?)");
        assertThat(result.parameters()).containsExactly("$.price");
    }

    @Test
    void withComplexJsonPath() {
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonExistsPsStrategy strategy = new MysqlJsonExistsPsStrategy();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("orders", "metadata"), Literal.of("$.items[0].id"));

        PreparedStatementSpec result = strategy.handle(jsonExists, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_CONTAINS_PATH(`metadata`, 'one', ?)");
        assertThat(result.parameters()).containsExactly("$.items[0].id");
    }
}
