package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class GenericCustomFunctionCallOptionsTest {

    @Test
    void rendersOptionsWithQuoting() {
        CustomFunctionCallOptions strategy = new GenericCustomFunctionCallOptions();
        String sql = strategy.renderOptions(Map.of("OPTION1", "value1", "OPTION2", 42));
        assertThat(sql).contains(" OPTION1 'value1'").contains(" OPTION2 42");
    }

    @Test
    void emptyOptionsReturnsEmptyString() {
        CustomFunctionCallOptions strategy = new GenericCustomFunctionCallOptions();
        String sql = strategy.renderOptions(Map.of());
        assertThat(sql).isEmpty();
    }
}
