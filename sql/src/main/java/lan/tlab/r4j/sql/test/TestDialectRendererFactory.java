package lan.tlab.r4j.sql.test;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginRegistry;
import lan.tlab.r4j.sql.plugin.builtin.mysql.MySQLDialectPlugin;
import lan.tlab.r4j.sql.plugin.builtin.oracle.OracleDialectPlugin;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.StandardSQLDialectPlugin;

/**
 * Test utility factory for creating SQL renderers via the plugin system.
 * <p>
 * This factory provides convenient methods for test code to obtain dialect renderers
 * without directly dealing with the plugin registry. It ensures all tests use the
 * same plugin-based approach for consistency.
 * <p>
 * <b>Why this class exists:</b>
 * <p>
 * This replaces the legacy {@code SqlRendererFactory} in test code, providing a
 * migration path from the old factory pattern to the new plugin-based architecture.
 * All production code should use {@link SqlDialectPluginRegistry} directly, but tests
 * can use this convenience class to reduce boilerplate.
 * <p>
 * <b>Usage in tests:</b>
 * <pre>{@code
 * @BeforeEach
 * void setUp() {
 *     // For SqlRenderer only
 *     renderer = TestDialectRendererFactory.standardSql2008();
 *
 *     // For complete DialectRenderer (SQL + PreparedStatement)
 *     dialectRenderer = TestDialectRendererFactory.dialectRendererMysql();
 * }
 * }</pre>
 *
 * @since 1.1
 */

// TODO: Consider deprecating this class in future versions to encourage direct use of SqlDialectPluginRegistry in
// tests.
public final class TestDialectRendererFactory {

    private static final SqlDialectPluginRegistry REGISTRY = SqlDialectPluginRegistry.createWithServiceLoader();

    private TestDialectRendererFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a {@link SqlRenderer} for Standard SQL:2008 dialect.
     *
     * @return SqlRenderer configured for Standard SQL:2008
     * @throws IllegalArgumentException if the StandardSQL plugin is not available
     */
    public static SqlRenderer standardSql2008() {
        return REGISTRY.getDialectRenderer(
                        StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow()
                .sqlRenderer();
    }

    /**
     * Creates a {@link SqlRenderer} for MySQL 8.x dialect.
     *
     * @return SqlRenderer configured for MySQL 8.x
     * @throws IllegalArgumentException if the MySQL plugin is not available
     */
    public static SqlRenderer mysql() {
        return REGISTRY.getDialectRenderer(MySQLDialectPlugin.DIALECT_NAME, MySQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow()
                .sqlRenderer();
    }

    /**
     * Creates a {@link SqlRenderer} for Oracle Database dialect.
     *
     * @return SqlRenderer configured for Oracle 19c+
     * @throws IllegalArgumentException if the Oracle plugin is not available
     */
    public static SqlRenderer oracle() {
        return REGISTRY.getDialectRenderer(OracleDialectPlugin.DIALECT_NAME, OracleDialectPlugin.DIALECT_VERSION)
                .orElseThrow()
                .sqlRenderer();
    }

    /**
     * Creates a complete {@link DialectRenderer} (SQL + PreparedStatement) for Standard SQL:2008.
     *
     * @return DialectRenderer configured for Standard SQL:2008
     * @throws IllegalArgumentException if the StandardSQL plugin is not available
     */
    public static DialectRenderer dialectRendererStandardSql2008() {
        return REGISTRY.getDialectRenderer(
                        StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
    }

    /**
     * Creates a complete {@link DialectRenderer} (SQL + PreparedStatement) for MySQL.
     *
     * @return DialectRenderer configured for MySQL 8.x
     * @throws IllegalArgumentException if the MySQL plugin is not available
     */
    public static DialectRenderer dialectRendererMysql() {
        return REGISTRY.getDialectRenderer(MySQLDialectPlugin.DIALECT_NAME, MySQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
    }

    /**
     * Creates a complete {@link DialectRenderer} (SQL + PreparedStatement) for Oracle.
     *
     * @return DialectRenderer configured for Oracle 19c+
     * @throws IllegalArgumentException if the Oracle plugin is not available
     */
    public static DialectRenderer dialectRendererOracle() {
        return REGISTRY.getDialectRenderer(OracleDialectPlugin.DIALECT_NAME, OracleDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
    }

    /**
     * Creates a {@link lan.tlab.r4j.sql.dsl.DSL} instance configured for Standard SQL:2008.
     * <p>
     * This method provides a convenient way for tests to obtain a DSL instance
     * using the default Standard SQL:2008 dialect without going through DSLRegistry.
     *
     * @return DSL instance configured for Standard SQL:2008
     * @throws IllegalArgumentException if the StandardSQL plugin is not available
     * @since 1.1
     */
    public static DSL dslStandardSql2008() {
        return new DSL(dialectRendererStandardSql2008());
    }
}
