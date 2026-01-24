package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.customfunction;

import java.util.Map;

/**
 * Strategy interface to render function options fragment for MySQL custom functions.
 * Implementations return the SQL fragment to append inside the function call,
 * starting with a leading space when options are present.
 */
public interface CustomFunctionCallOptions {
    /**
     * Renders the options fragment. Returns an empty string when no options.
     */
    String renderOptions(Map<String, Object> options);
}
