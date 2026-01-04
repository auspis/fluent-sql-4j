package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.util;

import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.dsl.DSL;
import io.github.massimiliano.fluentsql4j.plugin.SqlDialectPluginRegistry;
import io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.MysqlDialectPlugin;

/**
 * Test utility factory for creating MySQL renderers.
 * <p>
 * Provides convenient access to MySQL renderers for tests without boilerplate.
 * This class is specific to the MySQL plugin module and uses the plugin's constants.
 *
 * @since 1.0
 */
public final class MysqlUtil {

    private static final SqlDialectPluginRegistry REGISTRY = SqlDialectPluginRegistry.createWithServiceLoader();

    private MysqlUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a complete {@link PreparedStatementSpecFactory} (SQL + PreparedStatement) for MySQL.
     *
     * @return PreparedStatementSpecFactory configured for MySQL 8.x
     * @throws IllegalArgumentException if the MySQL plugin is not available
     */
    public static PreparedStatementSpecFactory preparedStatementSpecFactory() {
        return REGISTRY.getSpecFactory(MysqlDialectPlugin.DIALECT_NAME, MysqlDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
    }

    /**
     * Creates a {@link DSL} instance configured for MySQL 8.x.
     *
     * @return DSL instance configured for MySQL 8.x
     * @throws IllegalStateException if the MySQL plugin is not available
     */
    public static DSL dsl() {
        return new DSL(preparedStatementSpecFactory());
    }
}
