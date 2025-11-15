package lan.tlab.r4j.jdsql.plugin.builtin.sql2016;

import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry;

/**
 * Test utility factory for creating Standard SQL renderers.
 * <p>
 * Provides convenient access to Standard SQL renderers for tests without boilerplate.
 * This class is specific to the Standard SQL plugin module and uses the plugin's constants.
 *
 * @since 1.0
 */
public final class StandardSqlRendererFactory {

    private static final SqlDialectPluginRegistry REGISTRY = SqlDialectPluginRegistry.createWithServiceLoader();

    private StandardSqlRendererFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a {@link SqlRenderer} for Standard SQL:2008 dialect.
     *
     * @return SqlRenderer configured for Standard SQL:2008
     * @throws IllegalStateException if the StandardSQL plugin is not available
     */
    public static SqlRenderer standardSql() {
        return REGISTRY.getDialectRenderer(
                        StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow()
                .sqlRenderer();
    }

    /**
     * Creates a complete {@link DialectRenderer} (SQL + PreparedStatement) for Standard SQL:2008.
     *
     * @return DialectRenderer configured for Standard SQL:2008
     * @throws IllegalArgumentException if the StandardSQL plugin is not available
     */
    public static DialectRenderer dialectRendererStandardSql() {
        return REGISTRY.getDialectRenderer(
                        StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
    }

    /**
     * Creates a {@link DSL} instance configured for Standard SQL:2008.
     *
     * @return DSL instance configured for Standard SQL:2008
     * @throws IllegalStateException if the StandardSQL plugin is not available
     */
    public static DSL dslStandardSql() {
        return new DSL(dialectRendererStandardSql());
    }
}
