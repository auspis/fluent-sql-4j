package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GenericCustomFunctionCallOptionsTest {

    @Test
    void rendersOptionsWithParameterBinding() {
        CustomFunctionCallOptions strategy = new GenericCustomFunctionCallOptions();
        PreparedStatementSpec spec = strategy.renderOptions(Map.of("OPTION1", "value1", "OPTION2", 42));

        assertThat(spec.sql()).contains("OPTION1 ?").contains("OPTION2 ?");
        assertThat(spec.parameters()).containsExactlyInAnyOrder("value1", 42);
    }

    @Test
    void emptyOptionsReturnsEmptySpec() {
        CustomFunctionCallOptions strategy = new GenericCustomFunctionCallOptions();
        PreparedStatementSpec spec = strategy.renderOptions(Map.of());

        assertThat(spec.sql()).isEmpty();
        assertThat(spec.parameters()).isEmpty();
    }

    @Test
    void escapesStringValuesViaParameterBinding() {
        CustomFunctionCallOptions strategy = new GenericCustomFunctionCallOptions();
        PreparedStatementSpec spec = strategy.renderOptions(Map.of("OPTION1", "', DROP TABLE users--"));

        assertThat(spec.sql()).isEqualTo(" OPTION1 ?");
        assertThat(spec.parameters()).containsExactly("', DROP TABLE users--");
    }
}
