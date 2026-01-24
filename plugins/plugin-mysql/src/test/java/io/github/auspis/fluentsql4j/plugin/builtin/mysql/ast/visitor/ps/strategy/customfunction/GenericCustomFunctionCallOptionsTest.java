package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class GenericCustomFunctionCallOptionsTest {

    @Test
    void rendersOptionsWithQuoting() {
        CustomFunctionCallOptions strategy = new GenericCustomFunctionCallOptions();
        String sql = strategy.renderOptions(Map.of("OPTION1", "value1", "OPTION2", 42));
        assertThat(sql).contains(" OPTION1 'value1'");
        assertThat(sql).contains(" OPTION2 42");
    }

    @Test
    void emptyOptionsReturnsEmptyString() {
        CustomFunctionCallOptions strategy = new GenericCustomFunctionCallOptions();
        String sql = strategy.renderOptions(Map.of());
        assertThat(sql).isEmpty();
    }

    @Test
    void escapesSingleQuotesInStringValues() {
        CustomFunctionCallOptions strategy = new GenericCustomFunctionCallOptions();
        String sql = strategy.renderOptions(Map.of("OPTION", "it's a test"));
        assertThat(sql).isEqualTo(" OPTION 'it''s a test'");
    }

    @Test
    void preventsSqlInjectionWithSingleQuotes() {
        CustomFunctionCallOptions strategy = new GenericCustomFunctionCallOptions();
        String sql = strategy.renderOptions(Map.of("OPTION", "', DROP TABLE users--"));
        assertThat(sql).isEqualTo(" OPTION ''', DROP TABLE users--'");
    }

    @Test
    void escapesMultipleSingleQuotes() {
        CustomFunctionCallOptions strategy = new GenericCustomFunctionCallOptions();
        String sql = strategy.renderOptions(Map.of("OPTION", "it's John's book"));
        assertThat(sql).isEqualTo(" OPTION 'it''s John''s book'");
    }
}
