package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.StringLiteralEscapeStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.MysqlStringLiteralEscapeStrategy;
import java.util.Map;

/**
 * Generic options rendering strategy for MySQL custom functions.
 * Renders all options with proper string quoting, no special ordering.
 */
public class GenericCustomFunctionCallOptions implements CustomFunctionCallOptions {

    private final StringLiteralEscapeStrategy escapeStrategy;

    public GenericCustomFunctionCallOptions() {
        this(new MysqlStringLiteralEscapeStrategy());
    }

    public GenericCustomFunctionCallOptions(StringLiteralEscapeStrategy escapeStrategy) {
        this.escapeStrategy = escapeStrategy;
    }

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
        if (value instanceof String stringValue) {
            String escaped = escapeStrategy.escape(stringValue);
            sb.append('\'').append(escaped).append('\'');
        } else {
            sb.append(value);
        }
    }
}
