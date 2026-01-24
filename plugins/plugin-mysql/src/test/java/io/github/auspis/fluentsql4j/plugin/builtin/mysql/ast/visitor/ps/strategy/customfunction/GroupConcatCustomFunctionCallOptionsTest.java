package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GroupConcatCustomFunctionCallOptionsTest {

    @Test
    void rendersOrderByThenSeparatorWithParameterBinding() {
        CustomFunctionCallOptions strategy = new GroupConcatCustomFunctionCallOptions();
        PreparedStatementSpec spec = strategy.renderOptions(Map.of("SEPARATOR", ", ", "ORDER BY", "name"));

        assertThat(spec.sql()).startsWith(" ORDER BY ?").contains(" SEPARATOR ?");
        assertThat(spec.parameters()).containsExactly("name", ", ");
    }

    @Test
    void rendersOtherOptionsAfterReserved() {
        CustomFunctionCallOptions strategy = new GroupConcatCustomFunctionCallOptions();
        PreparedStatementSpec spec = strategy.renderOptions(Map.of("SEPARATOR", ";", "ORDER BY", "id", "OPTION1", 42));

        assertThat(spec.sql())
                .startsWith(" ORDER BY ?")
                .contains(" SEPARATOR ?")
                .contains(" OPTION1 ?");
        assertThat(spec.parameters()).containsExactly("id", ";", 42);
    }

    @Test
    void quotesNonStringValuesViaParameterBinding() {
        CustomFunctionCallOptions strategy = new GroupConcatCustomFunctionCallOptions();
        PreparedStatementSpec spec = strategy.renderOptions(Map.of("SEPARATOR", 1));

        assertThat(spec.sql()).isEqualTo(" SEPARATOR ?");
        assertThat(spec.parameters()).containsExactly(1);
    }

    @Test
    void escapesStringValuesViaParameterBinding() {
        CustomFunctionCallOptions strategy = new GroupConcatCustomFunctionCallOptions();
        PreparedStatementSpec spec = strategy.renderOptions(Map.of("SEPARATOR", "', DROP TABLE users--"));

        assertThat(spec.sql()).isEqualTo(" SEPARATOR ?");
        assertThat(spec.parameters()).containsExactly("', DROP TABLE users--");
    }
}
