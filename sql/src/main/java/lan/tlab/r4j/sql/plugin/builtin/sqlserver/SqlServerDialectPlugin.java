package lan.tlab.r4j.sql.plugin.builtin.sqlserver;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CurrentDateTimeRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.DateArithmeticRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.LegthRenderStrategy;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;

/**
 * Built-in plugin for the Microsoft SQL Server (T-SQL) dialect.
 * <p>
 * This plugin provides support for rendering SQL statements according to SQL Server
 * (Transact-SQL) syntax and semantics. SQL Server has several unique features and
 * syntax elements that differ from standard SQL.
 * <p>
 * <b>What is SQL Server?</b>
 * <p>
 * Microsoft SQL Server is a relational database management system developed by Microsoft.
 * It uses a variant of SQL called Transact-SQL (T-SQL) which includes procedural
 * programming extensions. This plugin implements SQL Server-specific SQL syntax.
 * <p>
 * <b>SQL Server Version Compatibility:</b>
 * <p>
 * This plugin is designed to work with SQL Server 2019 and later versions. It uses
 * the semantic version range "^15.0.0" which corresponds to SQL Server 2019.
 * SQL Server version numbering:
 * <ul>
 *   <li>SQL Server 2019 = Version 15.x</li>
 *   <li>SQL Server 2022 = Version 16.x</li>
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
 * Result<DialectRenderer> result = registry.getRenderer("sqlserver", "15.0.0");
 *
 * // Or created directly
 * SqlDialectPlugin plugin = SqlServerDialectPlugin.instance();
 * DialectRenderer renderer = plugin.createRenderer();
 * }</pre>
 * <p>
 * <b>SQL Server-Specific Features Implemented:</b>
 * <ul>
 *   <li><b>Identifier Escaping:</b> Uses square brackets ([table].[column]) instead of
 *       standard SQL double quotes</li>
 *   <li><b>Current Timestamp:</b> Uses GETDATE() instead of CURRENT_TIMESTAMP</li>
 *   <li><b>Date Arithmetic:</b> Uses DATEADD() function for date arithmetic</li>
 *   <li><b>String Length:</b> Uses LEN() function instead of LENGTH() or CHAR_LENGTH()</li>
 * </ul>
 * <p>
 * <b>Limitations and TODOs:</b>
 * <ul>
 *   <li>TODO: Implement SQL Server-specific pagination (OFFSET/FETCH or TOP)</li>
 *   <li>TODO: Implement SQL Server-specific string concatenation (+ operator)</li>
 *   <li>TODO: Implement SQL Server-specific data types (NVARCHAR, DATETIME2, etc.)</li>
 *   <li>TODO: Implement SQL Server-specific CAST/CONVERT differences</li>
 * </ul>
 * <p>
 * <b>ServiceLoader Discovery:</b>
 * <p>
 * This plugin is automatically discovered through Java's {@link java.util.ServiceLoader}
 * mechanism via {@link SqlServerDialectPluginProvider}. The provider is registered in
 * {@code META-INF/services/lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider}.
 *
 * @see SqlDialectPlugin
 * @see SqlServerDialectPluginProvider
 * @see <a href="https://docs.microsoft.com/en-us/sql/sql-server/">SQL Server Documentation</a>
 * @since 1.0
 */
public final class SqlServerDialectPlugin {

    /**
     * The canonical name for the SQL Server dialect.
     * <p>
     * This name is used for plugin registration and lookup in the
     * {@link lan.tlab.r4j.sql.plugin.SqlDialectPluginRegistry}.
     * The registry performs case-insensitive matching, so "sqlserver", "SQLServer",
     * "mssql", etc. can all be used.
     */
    public static final String DIALECT_NAME = "SQLServer";

    /**
     * The version range of SQL Server supported by this plugin.
     * <p>
     * This uses semantic versioning with a caret range "^15.0.0", which corresponds
     * to SQL Server 2019 (version 15.x) and later compatible versions.
     */
    public static final String DIALECT_VERSION = "^15.0.0";

    private static final SqlDialectPlugin INSTANCE =
            new SqlDialectPlugin(DIALECT_NAME, DIALECT_VERSION, SqlServerDialectPlugin::createSqlServerRenderer);

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class follows the singleton pattern. Use {@link #instance()} to obtain
     * the plugin instance.
     */
    private SqlServerDialectPlugin() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates the SQL Server-specific renderers.
     * <p>
     * This method creates both the {@link SqlRenderer} and {@link PreparedStatementRenderer}
     * configured specifically for SQL Server (T-SQL) syntax, including:
     * <ul>
     *   <li>Square bracket identifier escaping ([table].[column])</li>
     *   <li>GETDATE() for current timestamp</li>
     *   <li>DATEADD() for date arithmetic</li>
     *   <li>LEN() for string length</li>
     * </ul>
     * <p>
     * <b>Note:</b> This is a minimal implementation. Additional SQL Server-specific
     * features should be added as needed (see class-level TODOs).
     *
     * @return a new {@link DialectRenderer} instance configured for SQL Server, never {@code null}
     */
    private static DialectRenderer createSqlServerRenderer() {
        SqlRenderer sqlRenderer = SqlRenderer.builder()
                .escapeStrategy(EscapeStrategy.sqlServer())
                .currentDateTimeStrategy(CurrentDateTimeRenderStrategy.sqlServer())
                .dateArithmeticStrategy(DateArithmeticRenderStrategy.sqlServer())
                .lengthStrategy(LegthRenderStrategy.sqlServer())
                .build();

        PreparedStatementRenderer psRenderer =
                PreparedStatementRenderer.builder().sqlRenderer(sqlRenderer).build();

        return new DialectRenderer(sqlRenderer, psRenderer);
    }

    /**
     * Returns the singleton instance of the SQL Server dialect plugin.
     * <p>
     * This method is thread-safe and always returns the same instance. The plugin
     * is immutable and can be safely shared across threads.
     * <p>
     * <b>Example usage:</b>
     * <pre>{@code
     * SqlDialectPlugin plugin = SqlServerDialectPlugin.instance();
     * DialectRenderer renderer = plugin.createRenderer();
     * }</pre>
     *
     * @return the singleton SQL Server dialect plugin instance, never {@code null}
     */
    public static SqlDialectPlugin instance() {
        return INSTANCE;
    }
}
