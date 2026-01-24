package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Options rendering for MySQL GROUP_CONCAT function.
 * Enforces ORDER BY before SEPARATOR, then renders other options using parameter binding.
 */
public class GroupConcatCustomFunctionCallOptions implements CustomFunctionCallOptions {

    private static final String OPTION_ORDER_BY = "ORDER BY";
    private static final String OPTION_SEPARATOR = "SEPARATOR";

    @Override
    public PreparedStatementSpec renderOptions(Map<String, Object> options) {
        if (options == null || options.isEmpty()) {
            return new PreparedStatementSpec("", List.of());
        }

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (options.containsKey(OPTION_ORDER_BY)) {
            sql.append(" ").append(OPTION_ORDER_BY).append(" ");
            appendValue(sql, options.get(OPTION_ORDER_BY), params);
        }

        if (options.containsKey(OPTION_SEPARATOR)) {
            sql.append(" ").append(OPTION_SEPARATOR).append(" ?");
            params.add(options.get(OPTION_SEPARATOR));
        }

        for (Map.Entry<String, Object> entry : options.entrySet()) {
            String key = entry.getKey();
            if (!OPTION_ORDER_BY.equals(key) && !OPTION_SEPARATOR.equals(key)) {
                sql.append(" ").append(key).append(" ?");
                params.add(entry.getValue());
            }
        }

        return new PreparedStatementSpec(sql.toString(), params);
    }

    private void appendValue(StringBuilder sql, Object value, List<Object> params) {
        if (value == null) {
            sql.append("NULL");
        } else {
            sql.append("?");
            params.add(value);
        }
    }
}
