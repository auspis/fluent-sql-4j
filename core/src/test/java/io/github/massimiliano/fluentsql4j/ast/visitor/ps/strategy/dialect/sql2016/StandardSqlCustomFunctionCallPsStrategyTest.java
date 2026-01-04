package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.dialect.sql2016;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.CustomFunctionCallPsStrategy;
import io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCustomFunctionCallPsStrategy;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class StandardSqlCustomFunctionCallPsStrategyTest {
    private final CustomFunctionCallPsStrategy strategy = new StandardSqlCustomFunctionCallPsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();

    @Test
    void noArguments() {
        CustomFunctionCall function = new CustomFunctionCall("MY_FUNC", List.of(), Map.of());
        PreparedStatementSpec dto = strategy.handle(function, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("MY_FUNC()");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void singleArgument() {
        CustomFunctionCall function =
                new CustomFunctionCall("UPPER", List.of(ColumnReference.of("", "name")), Map.of());
        PreparedStatementSpec dto = strategy.handle(function, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("UPPER(\"name\")");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void multipleArguments() {
        CustomFunctionCall function = new CustomFunctionCall(
                "CONCAT",
                List.of(ColumnReference.of("", "first_name"), Literal.of(" "), ColumnReference.of("", "last_name")),
                Map.of());
        PreparedStatementSpec dto = strategy.handle(function, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("CONCAT(\"first_name\", ?, \"last_name\")");
        assertThat(dto.parameters()).containsExactly(" ");
    }

    @Test
    void withParameters() {
        CustomFunctionCall function = new CustomFunctionCall(
                "SUBSTR", List.of(ColumnReference.of("", "text"), Literal.of(1), Literal.of(10)), Map.of());
        PreparedStatementSpec dto = strategy.handle(function, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("SUBSTR(\"text\", ?, ?)");
        assertThat(dto.parameters()).containsExactly(1, 10);
    }
}
