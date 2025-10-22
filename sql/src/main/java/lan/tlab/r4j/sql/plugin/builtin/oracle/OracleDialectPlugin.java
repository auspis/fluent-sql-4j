package lan.tlab.r4j.sql.plugin.builtin.oracle;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CurrentDateTimeRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExceptRenderStrategy;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;

/**
 * Built-in plugin for the Oracle Database dialect.
 * <p>
 * This plugin provides support for rendering SQL statements according to Oracle Database
 * syntax and semantics. Oracle has several unique SQL syntax elements and features that
 * differ from standard SQL.
 * <p>
 * <b>What is Oracle Database?</b>
 * <p>
 * Oracle Database is a multi-model database management system produced and marketed by
 * Oracle Corporation. It is one of the most widely used enterprise relational database
 * systems. This plugin implements Oracle-specific SQL syntax and features.
 * <p>
 * <b>Oracle Version Compatibility:</b>
 * <p>
 * This plugin is designed to work with Oracle Database 19c and later versions. It uses
 * the semantic version range "^19.0.0" which matches all Oracle 19.x and 21.x versions.
 * <p>
 * <b>Design Characteristics:</b>
 * <ul>
 *   <li><b>Immutable</b>: This class is a singleton with no mutable state</li>
 *   <li><b>Thread-safe</b>: Can be safely used from multiple threads</li>
 *   <li><b>Stateless</b>: All rendering logic is delegated to the SqlRenderer</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Automatically discovered via ServiceLoader
 * SqlDialectRegistry registry = SqlDialectRegistry.createWithServiceLoader();
 * RegistryResult<DialectRenderer> result = registry.getRenderer("oracle", "19.0.0");
 *
 * // Or created directly
 * SqlDialectPlugin plugin = OracleDialectPlugin.instance();
 * DialectRenderer renderer = plugin.createRenderer();
 * }</pre>
 * <p>
 * <b>Oracle-Specific Features Implemented:</b>
 * <ul>
 *   <li><b>SYSDATE:</b> Uses SYSDATE instead of CURRENT_TIMESTAMP for current datetime</li>
 *   <li><b>MINUS:</b> Uses MINUS instead of EXCEPT for set difference operations</li>
 * </ul>
 * <p>
 * <b>Limitations and TODOs:</b>
 * <ul>
 *   <li>TODO: Implement Oracle-specific pagination (ROWNUM or FETCH FIRST)</li>
 *   <li>TODO: Implement Oracle-specific date arithmetic</li>
 *   <li>TODO: Implement Oracle-specific string concatenation (|| operator is standard)</li>
 *   <li>TODO: Implement Oracle-specific escape strategy (if needed)</li>
 *   <li>TODO: Implement Oracle-specific data types (VARCHAR2, NUMBER, etc.)</li>
 * </ul>
 * <p>
 * <b>ServiceLoader Discovery:</b>
 * <p>
 * This plugin is automatically discovered through Java's {@link java.util.ServiceLoader}
 * mechanism via {@link OracleDialectPluginProvider}. The provider is registered in
 * {@code META-INF/services/lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider}.
 *
 * @see SqlDialectPlugin
 * @see OracleDialectPluginProvider
 * @see <a href="https://docs.oracle.com/en/database/">Oracle Database Documentation</a>
 * @since 1.0
 */
public final class OracleDialectPlugin {

    /**
     * The canonical name for the Oracle dialect.
     * <p>
     * This name is used for plugin registration and lookup in the
     * {@link lan.tlab.r4j.sql.plugin.SqlDialectPluginRegistry}.
     * The registry performs case-insensitive matching.
     */
    public static final String DIALECT_NAME = "Oracle";

    /**
     * The version range of Oracle Database supported by this plugin.
     * <p>
     * This uses semantic versioning with a caret range "^19.0.0", which means it supports
     * Oracle 19.x and 21.x versions. Oracle uses different versioning schemes historically,
     * but modern versions (18c+) use year-based versioning that can be mapped to semver.
     */
    public static final String DIALECT_VERSION = "^19.0.0";

    private static final SqlDialectPlugin INSTANCE =
            new SqlDialectPlugin(DIALECT_NAME, DIALECT_VERSION, OracleDialectPlugin::createOracleRenderer);

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class follows the singleton pattern. Use {@link #instance()} to obtain
     * the plugin instance.
     */
    private OracleDialectPlugin() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates the Oracle-specific renderers.
     * <p>
     * This method creates both the {@link SqlRenderer} and {@link PreparedStatementRenderer}
     * configured specifically for Oracle syntax, including:
     * <ul>
     *   <li>SYSDATE for current timestamp</li>
     *   <li>MINUS for set difference (instead of EXCEPT)</li>
     * </ul>
     * <p>
     * <b>Note:</b> This is a minimal implementation. Additional Oracle-specific features
     * should be added as needed (see class-level TODOs).
     *
     * @return a new {@link DialectRenderer} instance configured for Oracle, never {@code null}
     */
    private static DialectRenderer createOracleRenderer() {
        SqlRenderer sqlRenderer = SqlRenderer.builder()
                .currentDateTimeStrategy(CurrentDateTimeRenderStrategy.oracle())
                .exceptStrategy(ExceptRenderStrategy.oracle())
                .build();

        PreparedStatementRenderer psRenderer =
                PreparedStatementRenderer.builder().sqlRenderer(sqlRenderer).build();

        return new DialectRenderer(sqlRenderer, psRenderer);
    }

    /**
     * Returns the singleton instance of the Oracle dialect plugin.
     * <p>
     * This method is thread-safe and always returns the same instance. The plugin
     * is immutable and can be safely shared across threads.
     * <p>
     * <b>Example usage:</b>
     * <pre>{@code
     * SqlDialectPlugin plugin = OracleDialectPlugin.instance();
     * DialectRenderer renderer = plugin.createRenderer();
     * }</pre>
     *
     * @return the singleton Oracle dialect plugin instance, never {@code null}
     */
    public static SqlDialectPlugin instance() {
        return INSTANCE;
    }
}
