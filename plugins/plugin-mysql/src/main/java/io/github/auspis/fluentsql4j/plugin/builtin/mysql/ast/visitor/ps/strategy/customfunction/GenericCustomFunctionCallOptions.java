package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generic options rendering strategy for MySQL custom functions.
 * Renders all options with proper parameter binding for security.
 */
public class GenericCustomFunctionCallOptions implements CustomFunctionCallOptions {

    @Override
    public PreparedStatementSpec renderOptions(Map<String, Object> options) {
        if (options == null || options.isEmpty()) {
            return new PreparedStatementSpec("", List.of());
        }

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Object> entry : options.entrySet()) {
            sql.append(" ").append(entry.getKey()).append(" ?");
            params.add(entry.getValue());
        }

        return new PreparedStatementSpec(sql.toString(), params);
    }
}
