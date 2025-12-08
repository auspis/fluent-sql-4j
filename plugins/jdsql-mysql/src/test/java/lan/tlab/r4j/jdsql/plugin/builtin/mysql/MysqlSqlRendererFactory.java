package lan.tlab.r4j.jdsql.plugin.builtin.mysql;

import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry;

/**
 * Test utility factory for creating MySQL renderers.
 * <p>
 * Provides convenient access to MySQL renderers for tests without boilerplate.
 * This class is specific to the MySQL plugin module and uses the plugin's constants.
 *
 * @since 1.0
 */
public final class MysqlSqlRendererFactory {

    private static final SqlDialectPluginRegistry REGISTRY = SqlDialectPluginRegistry.createWithServiceLoader();

    private MysqlSqlRendererFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a complete {@link PreparedStatementSpecFactory} (SQL + PreparedStatement) for MySQL.
     *
     * @return PreparedStatementSpecFactory configured for MySQL 8.x
     * @throws IllegalArgumentException if the MySQL plugin is not available
     */
    public static PreparedStatementSpecFactory dialectRendererMysql() {
        return REGISTRY.getSpecFactory(MysqlDialectPlugin.DIALECT_NAME, MysqlDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
    }

    /**
     * Creates a {@link DSL} instance configured for MySQL 8.x.
     *
     * @return DSL instance configured for MySQL 8.x
     * @throws IllegalStateException if the MySQL plugin is not available
     */
    public static DSL dslMysql() {
        return new DSL(dialectRendererMysql());
    }
}
