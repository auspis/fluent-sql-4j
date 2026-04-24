package io.github.auspis.fluentsql4j.plugin;

import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.functional.Result;
import io.github.auspis.fluentsql4j.hook.build.BuildHookFactory;
import java.util.Objects;

/**
 * Resolves DSL instances with a specific build hook factory policy.
 *
 * <p>This class bridges the gap between plugin metadata (via {@link SqlDialectPluginRegistry})
 * and DSL instantiation. It applies a specific {@link BuildHookFactory} policy when creating
 * DSL instances, ensuring that hooks are consistently applied at construction time rather than
 * as post-construction decoration.
 *
 * <p><b>Key Responsibilities:</b>
 * <ul>
 *   <li>Owns the {@link BuildHookFactory} policy for DSL creation
 *   <li>Coordinates plugin lookup and DSL factory invocation
 *   <li>Returns fully initialized DSL instances ready for use
 *   <li>Is stateless and creates a fresh DSL on each invocation
 * </ul>
 *
 * <p><b>Design Pattern:</b>
 * This class implements the Facade pattern, simplifying the interaction between
 * plugin metadata and DSL construction. It ensures clean separation of concerns:
 * <ul>
 *   <li>{@link SqlDialectPluginRegistry} is responsible for plugin discovery and matching
 *   <li>{@link SqlDialectResolver} is responsible for DSL assembly with hook policy
 *   <li>{@link io.github.auspis.fluentsql4j.dsl.DSLRegistry} is responsible for caching
 * </ul>
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * SqlDialectPluginRegistry pluginRegistry = SqlDialectPluginRegistry.createWithServiceLoader();
 * BuildHookFactory hookFactory = new ServiceLoaderBuildHookFactory();
 * SqlDialectResolver resolver = new SqlDialectResolver(pluginRegistry, hookFactory);
 *
 * Result<DSL> result = resolver.resolve("mysql", "8.0.35");
 * }</pre>
 *
 * <p><b>Thread Safety:</b>
 * This class is thread-safe. Each call to {@link #resolve} creates a fresh DSL instance
 * and does not maintain mutable state.
 *
 * @see SqlDialectPluginRegistry
 * @see SqlDialectPlugin
 * @see DSL
 * @see BuildHookFactory
 * @since 1.4
 */
public final class SqlDialectResolver {

    private final SqlDialectPluginRegistry pluginRegistry;
    private final BuildHookFactory buildHookFactory;

    /**
     * Creates a resolver with the specified plugin registry and hook factory.
     *
     * @param pluginRegistry the plugin registry to use for dialect resolution; must not be {@code
     *     null}
     * @param buildHookFactory the hook factory to apply when creating DSL instances; must not be
     *     {@code null}
     * @throws NullPointerException if either parameter is {@code null}
     */
    public SqlDialectResolver(SqlDialectPluginRegistry pluginRegistry, BuildHookFactory buildHookFactory) {
        this.pluginRegistry = Objects.requireNonNull(pluginRegistry, "SqlDialectPluginRegistry must not be null");
        this.buildHookFactory = Objects.requireNonNull(buildHookFactory, "BuildHookFactory must not be null");
    }

    /**
     * Resolves and creates a DSL instance for the specified dialect and version.
     *
     * <p>This method:
     * <ol>
     *   <li>Looks up the matching plugin via the plugin registry
     *   <li>Creates a fresh DSL instance from the plugin with the configured hook factory
     *   <li>Returns the fully initialized DSL ready for use
     * </ol>
     *
     * <p>The returned DSL instance is complete and requires no further initialization.
     * Hooks are applied during construction, not as post-construction decoration.
     *
     * <p><b>Caching:</b> This method does not cache results. Caching is the responsibility
     * of the caller (typically {@link io.github.auspis.fluentsql4j.dsl.DSLRegistry}).
     *
     * <p><b>Version Matching:</b> Version matching is delegated to the plugin registry
     * and follows its configured matching strategy (SemVer or exact match).
     *
     * @param dialect the name of the SQL dialect; must not be {@code null}
     * @param version the database version; may be {@code null} to match any version
     * @return a result containing a fully initialized DSL instance, or a failure if no
     *     matching plugin is found
     * @throws NullPointerException if {@code dialect} is {@code null}
     */
    public Result<DSL> resolve(String dialect, String version) {
        Objects.requireNonNull(dialect, "Dialect must not be null");

        return pluginRegistry.getPlugin(dialect, version).map(plugin -> plugin.createDSL(buildHookFactory));
    }

    /**
     * Resolves and creates a DSL instance for the specified dialect without version matching.
     *
     * @param dialect the name of the SQL dialect; must not be {@code null}
     * @return a result containing the DSL instance, or a failure if the dialect is not supported
     */
    public Result<DSL> resolve(String dialect) {
        return resolve(dialect, null);
    }
}
