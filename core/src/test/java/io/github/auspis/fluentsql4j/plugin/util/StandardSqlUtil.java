package io.github.auspis.fluentsql4j.plugin.util;

import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPluginRegistry;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin;

/**
 * Test utility factory for creating Standard SQL renderers.
 * <p>
 * Provides convenient access to Standard SQL renderers for tests without boilerplate.
 * This class is specific to the Standard SQL plugin module and uses the plugin's constants.
 *
 * @since 1.0
 */
public final class StandardSqlUtil {

    private static final SqlDialectPluginRegistry REGISTRY = SqlDialectPluginRegistry.createWithServiceLoader();

    private StandardSqlUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a complete {@link PreparedStatementSpecFactory} (SQL + PreparedStatement) for Standard SQL:2008.
     *
     * @return PreparedStatementSpecFactory configured for Standard SQL:2008
     * @throws IllegalArgumentException if the StandardSQL plugin is not available
     */
    public static PreparedStatementSpecFactory preparedStatementSpecFactory() {
        return REGISTRY.getSpecFactory(StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
    }

    /**
     * Creates a {@link DSL} instance configured for Standard SQL:2008.
     *
     * @return DSL instance configured for Standard SQL:2008
     * @throws IllegalStateException if the StandardSQL plugin is not available
     */
    public static DSL dsl() {
        return new DSL(preparedStatementSpecFactory());
    }
}
