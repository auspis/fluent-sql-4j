package lan.tlab.r4j.jdsql.plugin.builtin.postgre;

import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry;

/**
 * Test utility factory for creating PostgreSQL renderers.
 * <p>
 * Provides convenient access to PostgreSQL renderers for tests without boilerplate.
 * This class is specific to the PostgreSQL plugin module and uses the plugin's constants.
 *
 * @since 1.0
 */
public final class PostgreSqlRendererFactory {

    private static final SqlDialectPluginRegistry REGISTRY = SqlDialectPluginRegistry.createWithServiceLoader();

    private PostgreSqlRendererFactory() {}

    /**
     * Creates a complete {@link PreparedStatementSpecFactory} (SQL + PreparedStatement) for PostgreSQL.
     *
     * @return PreparedStatementSpecFactory configured for PostgreSQL 15.x
     * @throws IllegalArgumentException if the PostgreSQL plugin is not available
     */
    public static PreparedStatementSpecFactory dialectRendererPostgreSql() {
        return REGISTRY.getSpecFactory(PostgreSqlDialectPlugin.DIALECT_NAME, PostgreSqlDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
    }

    /**
     * Creates a {@link DSL} instance configured for PostgreSQL 15.x.
     *
     * @return DSL instance configured for PostgreSQL 15.x
     * @throws IllegalStateException if the PostgreSQL plugin is not available
     */
    public static DSL dslPostgreSql() {
        return new DSL(dialectRendererPostgreSql());
    }
}
