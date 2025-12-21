package lan.tlab.r4j.jdsql.plugin;

/**
 * Service provider interface for SQL dialect plugins.
 * <p>
 * Implementations of this interface are discovered via Java {@link java.util.ServiceLoader}.
 * Each provider creates and returns a {@link SqlDialectPlugin} instance.
 * <p>
 * To make your plugin discoverable, create a provider file:
 * {@code META-INF/services/lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider}
 * containing the fully qualified name of your provider implementation.
 * <p>
 * <b>Example implementation:</b>
 * <pre>{@code
 * public class MySqlPluginProvider implements SqlDialectPluginProvider {
 *     @Override
 *     public SqlDialectPlugin get() {
 *         return new SqlDialectPlugin(
 *             "mysql",
 *             "^8.0.0",
 *             () -> new MysqlDSL(createPreparedStatementSpecFactory())
 *         );
 *     }
 *
 *     private PreparedStatementSpecFactory createPreparedStatementSpecFactory() {
 *         // Build and return the dialect-specific factory
 *     }
 * }
 * }</pre>
 *
 * @see SqlDialectPlugin
 * @see SqlDialectPluginRegistry
 * @see java.util.ServiceLoader
 * @since 1.0
 */
@FunctionalInterface
public interface SqlDialectPluginProvider {

    /**
     * Creates and returns a SQL dialect plugin instance.
     * <p>
     * The returned plugin must be fully configured and validated.
     * The plugin's dialect name and version will be validated by the registry
     * at registration time.
     *
     * @return a fully configured and validated SQL dialect plugin, never {@code null}
     */
    SqlDialectPlugin get();
}
