package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.core.expression.function.json.BehaviorKind;
import lan.tlab.r4j.jdsql.ast.core.expression.function.json.JsonExists;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.Test;

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
