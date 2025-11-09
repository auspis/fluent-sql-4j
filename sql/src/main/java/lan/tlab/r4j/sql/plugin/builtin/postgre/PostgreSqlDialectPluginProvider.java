package lan.tlab.r4j.sql.plugin.builtin.postgre;

import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider;

/**
 * Service provider for the PostgreSQL dialect plugin.
 * <p>
 * This class enables automatic discovery of the PostgreSQL dialect plugin through
 * Java's {@link java.util.ServiceLoader} mechanism. It is registered in the
 * {@code META-INF/services/lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider} file.
 * <p>
 * <b>Service Loading Process:</b>
 * <ol>
 *   <li>The {@link lan.tlab.r4j.sql.plugin.SqlDialectPluginRegistry} uses
 *       {@link java.util.ServiceLoader} to discover all available providers</li>
 *   <li>For each provider found, the registry calls {@link #get()}</li>
 *   <li>The returned plugin is registered and made available for use</li>
 * </ol>
 * <p>
 * <b>Usage:</b>
 * <p>
 * Developers do not typically interact with this class directly. Instead, the plugin
 * is discovered automatically when creating a registry:
 * <pre>{@code
 * SqlDialectRegistry registry = SqlDialectRegistry.createWithServiceLoader();
 * // PostgreSQL plugin is now available in the registry
 * }</pre>
 *
 * @see SqlDialectPluginProvider
 * @see PostgreSqlDialectPlugin
 * @see java.util.ServiceLoader
 * @since 1.0
 */
public final class PostgreSqlDialectPluginProvider implements SqlDialectPluginProvider {

    /**
     * Returns the PostgreSQL dialect plugin instance.
     * <p>
     * This method is called by the {@link java.util.ServiceLoader} during plugin discovery.
     *
     * @return the PostgreSQL dialect plugin, never {@code null}
     */
    @Override
    public SqlDialectPlugin get() {
        return PostgreSqlDialectPlugin.instance();
    }
}
