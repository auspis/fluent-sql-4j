package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016;

import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.plugin.SqlDialectPlugin;
import io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlEscapeStrategy;

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
 *   <li><b>Stateless</b>: Rendering logic lives in the {@link PreparedStatementSpecFactory} strategies</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Automatically discovered via ServiceLoader
 * SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();
 * Result<PreparedStatementSpecFactory> result = registry.getSpecFactory("standardsql", "2008");
 *
 * // Or created directly
 * SqlDialectPlugin plugin = StandardSQLDialectPlugin.instance();
 * PreparedStatementSpecFactory specFactory = plugin.createDSL().getSpecFactory();
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
     * This name is used for plugin registration and lookup in the {@link io.github.massimiliano.fluentsql4j.plugin.SqlDialectPluginRegistry}.
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
     * Creates a {@link PreparedStatementSpecFactory} for Standard SQL.
     * <p>
     * The factory wires Standard SQL rendering strategies into the {@link AstToPreparedStatementSpecVisitor}.
     *
     * @return a new PreparedStatementSpecFactory instance
     */
    private static PreparedStatementSpecFactory standardSqlPsSpecFacory() {
        AstToPreparedStatementSpecVisitor astToPsSpecVisitor = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new StandardSqlEscapeStrategy())
                .build();

        return new PreparedStatementSpecFactory(astToPsSpecVisitor);
    }

    /**
     * Creates a DSL instance for Standard SQL:2008.
     * <p>
     * Returns the base {@link io.github.massimiliano.fluentsql4j.dsl.DSL} class configured with
     * the Standard SQL:2008 PreparedStatement spec factory.
     *
     * @return a new {@link io.github.massimiliano.fluentsql4j.dsl.DSL} instance, never {@code null}
     */
    private static io.github.massimiliano.fluentsql4j.dsl.DSL createStandardSql2008DSL() {
        return new io.github.massimiliano.fluentsql4j.dsl.DSL(standardSqlPsSpecFacory());
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
     * PreparedStatementSpecFactory specFactory = plugin.createDSL().getSpecFactory();
     * }</pre>
     *
     * @return the singleton Standard SQL:2008 dialect plugin instance, never {@code null}
     */
    public static SqlDialectPlugin instance() {
        return INSTANCE;
    }
}
