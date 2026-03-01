package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.data.MysqlFunctionCallNames;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Options rendering for MySQL GROUP_CONCAT function.
 * Enforces ORDER BY before SEPARATOR, then renders other options using parameter binding.
 */
public class GroupConcatCustomFunctionCallOptions implements CustomFunctionCallOptions {

    @Override
    public PreparedStatementSpec renderOptions(Map<String, Object> options) {
        if (options == null || options.isEmpty()) {
            return new PreparedStatementSpec("", List.of());
        }

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (options.containsKey(MysqlFunctionCallNames.Options.ORDER_BY)) {
            sql.append(" ORDER BY ");
            appendValue(sql, options.get(MysqlFunctionCallNames.Options.ORDER_BY), params);
        }

        if (options.containsKey(MysqlFunctionCallNames.Options.SEPARATOR)) {
            sql.append(" ").append(MysqlFunctionCallNames.Options.SEPARATOR).append(" ?");
            params.add(options.get(MysqlFunctionCallNames.Options.SEPARATOR));
        }

        for (Map.Entry<String, Object> entry : options.entrySet()) {
            String key = entry.getKey();
            if (!MysqlFunctionCallNames.Options.ORDER_BY.equals(key)
                    && !MysqlFunctionCallNames.Options.SEPARATOR.equals(key)) {
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
