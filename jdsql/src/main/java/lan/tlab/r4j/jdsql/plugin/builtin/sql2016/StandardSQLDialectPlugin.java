package lan.tlab.r4j.jdsql.plugin.builtin.sql2016;

import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPlugin;

/**
 * Built-in plugin for the Standard SQL:2008 dialect.
 * <p>
 * This plugin provides support for rendering SQL statements according to the SQL:2008 standard
 * specification. It serves as the default dialect for backward compatibility and as a reference
 * implementation for other plugin developers.
 * <p>
 * <b>What is Standard SQL:2008?</b>
 * <p>
 * SQL:2008 (formally ISO/IEC 9075:2008) is the seventh revision of the ISO and ANSI standard
 * for the SQL database query language. This plugin implements a subset of SQL:2008 features
 * commonly supported across major database systems, focusing on:
 * <ul>
 *   <li>Core SQL data manipulation (SELECT, INSERT, UPDATE, DELETE)</li>
 *   <li>Standard SQL functions and operators</li>
 *   <li>Common table expressions (CTEs)</li>
 *   <li>Window functions</li>
 *   <li>Standard pagination using OFFSET/FETCH</li>
 * </ul>
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
 * Result<DialectRenderer> result = registry.getRenderer("standardsql", "2008");
 *
 * // Or created directly
 * SqlDialectPlugin plugin = StandardSQLDialectPlugin.instance();
 * DialectRenderer renderer = plugin.createRenderer();
 * }</pre>
 * <p>
 * <b>Version Matching:</b>
 * <p>
 * This plugin uses exact version matching with version "2008". Since SQL:2008 is not
 * a semantic version, the registry will use exact string matching when resolving this plugin.
 * <p>
 * <b>ServiceLoader Discovery:</b>
 * <p>
 * This plugin is automatically discovered through Java's {@link java.util.ServiceLoader}
 * mechanism via {@link StandardSQLDialectPluginProvider}. The provider is registered in
 * {@code META-INF/services/lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider}.
 *
 * @see SqlDialectPlugin
 * @see StandardSQLDialectPluginProvider
 * @see <a href="https://en.wikipedia.org/wiki/SQL:2008">SQL:2008 Standard</a>
 * @since 1.0
 */
public final class StandardSQLDialectPlugin {

    /**
     * The canonical name for the Standard SQL dialect.
     * <p>
     * This name is used for plugin registration and lookup in the {@link lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry}.
     * The registry performs case-insensitive matching, so "StandardSQL", "standardsql", and "STANDARDSQL"
     * will all match this dialect.
     */
    public static final String DIALECT_NAME = "StandardSQL";

    /**
     * The version of the SQL standard supported by this plugin.
     * <p>
     * This corresponds to SQL:2008 (ISO/IEC 9075:2008). Since this is not a semantic version,
     * the registry will use exact string matching when resolving this plugin.
     */
    public static final String DIALECT_VERSION = "2008";

    private static final SqlDialectPlugin INSTANCE =
            new SqlDialectPlugin(DIALECT_NAME, DIALECT_VERSION, StandardSQLDialectPlugin::createStandardSql2008DSL);

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class follows the singleton pattern. Use {@link #instance()} to obtain
     * the plugin instance.
     */
    private StandardSQLDialectPlugin() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a {@link DialectRenderer} for Standard SQL:2008.
     * <p>
     * This method creates both SQL and PreparedStatement renderers configured
     * for the SQL:2008 standard, ensuring consistency between the two.
     *
     * @return a new DialectRenderer instance
     */
    private static DialectRenderer createStandardSql2008Renderer() {
        SqlRenderer sqlRenderer = SqlRenderer.builder().build();

        PreparedStatementRenderer psRenderer =
                PreparedStatementRenderer.builder().sqlRenderer(sqlRenderer).build();

        return new DialectRenderer(sqlRenderer, psRenderer);
    }

    /**
     * Creates a DSL instance for Standard SQL:2008.
     * <p>
     * Returns the base {@link lan.tlab.r4j.jdsql.dsl.DSL} class configured with
     * the Standard SQL:2008 renderer.
     *
     * @return a new {@link lan.tlab.r4j.jdsql.dsl.DSL} instance, never {@code null}
     */
    private static lan.tlab.r4j.jdsql.dsl.DSL createStandardSql2008DSL() {
        return new lan.tlab.r4j.jdsql.dsl.DSL(createStandardSql2008Renderer());
    }

    /**
     * Returns the singleton instance of the Standard SQL:2008 dialect plugin.
     * <p>
     * This method is thread-safe and always returns the same instance. The plugin
     * is immutable and can be safely shared across threads.
     * <p>
     * <b>Example usage:</b>
     * <pre>{@code
     * SqlDialectPlugin plugin = StandardSQLDialectPlugin.instance();
     * DialectRenderer renderer = plugin.createRenderer();
     * }</pre>
     *
     * @return the singleton Standard SQL:2008 dialect plugin instance, never {@code null}
     */
    public static SqlDialectPlugin instance() {
        return INSTANCE;
    }
}
