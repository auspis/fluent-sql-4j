package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.util.MysqlStringUtil;
import java.util.Map;

/**
 * Options rendering for MySQL GROUP_CONCAT function.
 * Enforces ORDER BY before SEPARATOR, then renders other options.
 */
public class GroupConcatCustomFunctionCallOptions implements CustomFunctionCallOptions {

    private static final String OPTION_ORDER_BY = "ORDER BY";
    private static final String OPTION_SEPARATOR = "SEPARATOR";

    @Override
    public String renderOptions(Map<String, Object> options) {
        if (options == null || options.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (options.containsKey(OPTION_ORDER_BY)) {
            appendOption(sb, OPTION_ORDER_BY, options.get(OPTION_ORDER_BY));
        }
        if (options.containsKey(OPTION_SEPARATOR)) {
            appendOption(sb, OPTION_SEPARATOR, options.get(OPTION_SEPARATOR));
        }
        for (Map.Entry<String, Object> entry : options.entrySet()) {
            String key = entry.getKey();
            if (!OPTION_ORDER_BY.equals(key) && !OPTION_SEPARATOR.equals(key)) {
                appendOption(sb, key, entry.getValue());
            }
        }
        return sb.toString();
    }

    private void appendOption(StringBuilder sb, String key, Object value) {
        sb.append(" ").append(key).append(" ");
        appendValue(sb, value);
    }

    private void appendValue(StringBuilder sb, Object value) {
        if (value instanceof String s) {
            String escaped = MysqlStringUtil.escape(s);
            sb.append('\'').append(escaped).append('\'');
        } else {
            sb.append(value);
        }
    }
}
