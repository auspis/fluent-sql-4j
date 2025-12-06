package lan.tlab.r4j.jdsql.plugin.builtin.mysql;

import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry;

/**
 * Test utility factory for creating MySQL PreparedStatement renderers.
 * <p>
 * Provides convenient access to MySQL PreparedStatement renderers for tests without boilerplate.
 * This class is specific to the MySQL plugin module and uses the plugin's constants.
 *
 * @since 1.0
 */
public final class MysqlPreparedStatementRendererFactory {

    private static final SqlDialectPluginRegistry REGISTRY = SqlDialectPluginRegistry.createWithServiceLoader();

    private MysqlPreparedStatementRendererFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a {@link PreparedStatementRenderer} for MySQL 8.x dialect.
     *
     * @return PreparedStatementRenderer configured for MySQL 8.x
     * @throws IllegalStateException if the MySQL plugin is not available
     */
    public static PreparedStatementRenderer create() {
        return REGISTRY.getDialectRenderer(MysqlDialectPlugin.DIALECT_NAME, MysqlDialectPlugin.DIALECT_VERSION)
                .orElseThrow()
                .psRenderer();
    }

    /**
     * Creates a complete {@link DialectRenderer} (PreparedStatement) for MySQL.
     *
     * @return DialectRenderer configured for MySQL 8.x
     * @throws IllegalArgumentException if the MySQL plugin is not available
     */
    public static DialectRenderer dialectRendererMysql() {
        return REGISTRY.getDialectRenderer(MysqlDialectPlugin.DIALECT_NAME, MysqlDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
    }
}
