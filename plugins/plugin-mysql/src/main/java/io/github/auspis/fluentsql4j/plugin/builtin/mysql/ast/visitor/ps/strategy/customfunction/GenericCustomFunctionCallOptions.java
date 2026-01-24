package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import java.util.Map;

/**
 * Generic options rendering strategy for MySQL custom functions.
 * Renders all options with proper string quoting, no special ordering.
 */
public class GenericCustomFunctionCallOptions implements CustomFunctionCallOptions {

    @Override
    public String renderOptions(Map<String, Object> options) {
        if (options == null || options.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : options.entrySet()) {
            sb.append(" ").append(entry.getKey()).append(" ");
            appendValue(sb, entry.getValue());
        }
        return sb.toString();
    }

    private void appendValue(StringBuilder sb, Object value) {
        if (value instanceof String) {
            sb.append('\'').append(value).append('\'');
        } else {
            sb.append(value);
        }
    }
}
