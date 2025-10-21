package lan.tlab.r4j.sql.plugin.builtin.standardsql2008;

import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider;

/**
 * Service provider for the Standard SQL:2008 dialect plugin.
 * <p>
 * This provider enables automatic discovery of the {@link StandardSQLDialectPlugin}
 * through Java's {@link java.util.ServiceLoader} mechanism. It is registered in
 * {@code META-INF/services/lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider}.
 * <p>
 * <b>ServiceLoader Registration:</b>
 * <p>
 * To enable automatic discovery, this provider class is listed in the service provider
 * configuration file at:
 * <pre>
 * META-INF/services/lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider
 * </pre>
 * <p>
 * <b>Usage:</b>
 * <p>
 * Developers do not typically interact with this class directly. Instead, the plugin
 * is automatically discovered when creating a registry with ServiceLoader:
 * <pre>{@code
 * SqlDialectRegistry registry = SqlDialectRegistry.createWithServiceLoader();
 * // StandardSQLDialectPlugin is now registered and available
 * }</pre>
 * <p>
 * <b>Thread Safety:</b>
 * <p>
 * This provider is stateless and thread-safe. The ServiceLoader framework may create
 * multiple instances, but since the provider is stateless, this has no adverse effects.
 *
 * @see StandardSQLDialectPlugin
 * @see SqlDialectPluginProvider
 * @see java.util.ServiceLoader
 * @since 1.0
 */
public final class StandardSQLDialectPluginProvider implements SqlDialectPluginProvider {

    /**
     * Returns the Standard SQL:2008 dialect plugin instance.
     * <p>
     * This method is called by the {@link java.util.ServiceLoader} framework during
     * plugin discovery. It returns the singleton instance of the StandardSQLDialectPlugin.
     *
     * @return the Standard SQL:2008 dialect plugin, never {@code null}
     */
    @Override
    public SqlDialectPlugin get() {
        return StandardSQLDialectPlugin.instance();
    }
}
