package lan.tlab.r4j.jdsql.plugin.builtin.oracle;

import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry;

/**
 * Test utility factory for creating Oracle renderers.
 * <p>
 * Provides convenient access to Oracle renderers for tests without boilerplate.
 * This class is specific to the Oracle plugin module and uses the plugin's constants.
 *
 * @since 1.0
 */
public final class OracleSqlRendererFactory {

    private static final SqlDialectPluginRegistry REGISTRY = SqlDialectPluginRegistry.createWithServiceLoader();

    private OracleSqlRendererFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a complete {@link PreparedStatementSpecFactory} (SQL + PreparedStatement) for Oracle.
     *
     * @return PreparedStatementSpecFactory configured for Oracle 19c+
     * @throws IllegalArgumentException if the Oracle plugin is not available
     */
    public static PreparedStatementSpecFactory dialectRendererOracle() {
        return REGISTRY.getSpecFactory(OracleDialectPlugin.DIALECT_NAME, OracleDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
    }

    /**
     * Creates a {@link DSL} instance configured for Oracle 19c+.
     *
     * @return DSL instance configured for Oracle 19c+
     * @throws IllegalStateException if the Oracle plugin is not available
     */
    public static DSL dslOracle() {
        return new DSL(dialectRendererOracle());
    }
}
