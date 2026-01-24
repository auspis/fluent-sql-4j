package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class GroupConcatCustomFunctionCallOptionsTest {

    @Test
    void rendersOrderByThenSeparator() {
        CustomFunctionCallOptions strategy = new GroupConcatCustomFunctionCallOptions();
        String sql = strategy.renderOptions(Map.of("SEPARATOR", ", ", "ORDER BY", "name"));
        assertThat(sql).isEqualTo(" ORDER BY 'name' SEPARATOR ', '");
    }

    @Test
    void rendersOtherOptionsAfterReserved() {
        CustomFunctionCallOptions strategy = new GroupConcatCustomFunctionCallOptions();
        String sql = strategy.renderOptions(Map.of("SEPARATOR", ";", "ORDER BY", "id", "OPTION1", 42));
        assertThat(sql).startsWith(" ORDER BY 'id' SEPARATOR ';'").contains(" OPTION1 42");
    }

    @Test
    void quotesStringValuesAndLeavesNonStringAsIs() {
        CustomFunctionCallOptions strategy = new GroupConcatCustomFunctionCallOptions();
        String sql = strategy.renderOptions(Map.of("SEPARATOR", 1));
        assertThat(sql).isEqualTo(" SEPARATOR 1");
    }
}
