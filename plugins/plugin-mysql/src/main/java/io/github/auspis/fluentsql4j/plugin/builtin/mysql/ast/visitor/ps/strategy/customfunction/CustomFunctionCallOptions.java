package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.Map;

/**
 * Strategy interface to render function options fragment for MySQL custom functions.
 * Implementations return a PreparedStatementSpec containing the SQL fragment and bound parameters.
 * Uses parameter binding (?) instead of inline literals for security.
 */
public interface CustomFunctionCallOptions {
    /**
     * Renders the options fragment with parameter binding.
     * Returns a PreparedStatementSpec with empty SQL when no options are present.
     */
    PreparedStatementSpec renderOptions(Map<String, Object> options);
}
