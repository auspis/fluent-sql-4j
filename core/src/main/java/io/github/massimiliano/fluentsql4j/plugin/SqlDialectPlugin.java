package io.github.massimiliano.fluentsql4j.plugin;

import io.github.massimiliano.fluentsql4j.dsl.DSL;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Immutable record representing a SQL dialect plugin.
 * <p>
 * This record provides automatic immutability, value semantics, and fail-fast validation
 * through its compact constructor. All fields are validated at construction time,
 * ensuring that invalid plugins cannot be created.
 * <p>
 * Plugins are discovered and registered automatically via Java's {@link java.util.ServiceLoader}
 * mechanism through {@link SqlDialectPluginProvider} implementations.
 * <p>
 * <b>Version Support:</b>
 * <p>
 * Plugins declare version support using either Semantic Versioning (SemVer) notation or
 * exact version strings for non-SemVer versions. The registry uses this information to match
 * plugins to requested database versions. Multiple plugins can be registered for the same
 * dialect with different version ranges.
 * <p>
 * <b>Example usage with SemVer:</b>
 * <pre>{@code
 * var plugin = new SqlDialectPlugin(
 *     "mysql",
 *     "^8.0.0",  // Supports all MySQL 8.x versions
 *     MySQLDialectPlugin::createMySqlDSL
 * );
 * }</pre>
 * <p>
 * <b>Example usage with non-SemVer (exact match):</b>
 * <pre>{@code
 * var plugin = new SqlDialectPlugin(
 *     "standardsql",
 *     "2008",  // Supports exactly SQL:2008 standard
 *     StandardSQLDialectPlugin::createStandardSql2008DSL
 * );
 * }</pre>
 * <p>
 * <b>Supported version formats:</b>
 * <ul>
 *   <li>SemVer exact version: {@code "8.0.35"}</li>
 *   <li>SemVer caret range (compatible): {@code "^8.0.0"} (matches {@code >=8.0.0 <9.0.0})</li>
 *   <li>SemVer tilde range (patch): {@code "~5.7.42"} (matches {@code >=5.7.42 <5.8.0})</li>
 *   <li>SemVer explicit range: {@code ">=8.0.0 <9.0.0"}</li>
 *   <li>SemVer compound conditions: {@code ">=5.7.0 <8.0.0 || >=8.0.20"}</li>
 *   <li>Non-SemVer exact match: {@code "2008"}, {@code "2011"}, {@code "2016"}</li>
 * </ul>
 *
 * @param dialectName the canonical name of the SQL dialect in lowercase (e.g., "mysql", "postgresql", "standardsql")
 * @param dialectVersion the version this plugin supports - either a SemVer range (e.g., "^8.0.0") or exact version string (e.g., "2008")
 * @param dslSupplier a supplier that creates new {@link DSL} instances for this dialect, may return the base DSL or a dialect-specific extension
 * @see SqlDialectPluginProvider
 * @see SqlDialectPluginRegistry
 * @see DSL
 * @see <a href="https://semver.org/">Semantic Versioning</a>
 * @see <a href="https://github.com/npm/node-semver">NPM semver ranges</a>
 * @since 1.0
 */
public record SqlDialectPlugin(String dialectName, String dialectVersion, Supplier<DSL> dslSupplier) {

    /**
     * Compact constructor with validation.
     * <p>
     * Validates that all parameters are non-null and that the dialect version
     * is not empty. Version can be either a valid SemVer range or an exact version
     * string for non-SemVer versions. The registry will determine the matching strategy
     * based on whether the version is SemVer-compatible.
     *
     * @throws NullPointerException if any parameter is {@code null}
     * @throws IllegalArgumentException if {@code dialectVersion} is empty or blank
     */
    public SqlDialectPlugin {
        Objects.requireNonNull(dialectName, "Dialect name must not be null");
        Objects.requireNonNull(dialectVersion, "Dialect version must not be null");
        Objects.requireNonNull(dslSupplier, "DSL supplier must not be null");

        if (dialectVersion.isBlank()) {
            throw new IllegalArgumentException("Dialect version must not be blank in plugin '" + dialectName + "'");
        }
    }

    /**
     * Creates a {@link DSL} instance configured for this SQL dialect.
     * <p>
     * The DSL provides a fluent, type-safe API for building SQL queries. This method
     * returns either the base {@link DSL} class or a dialect-specific extension that
     * provides additional custom functions (e.g., {@code MySQLDSL} with MySQL-specific
     * functions like {@code GROUP_CONCAT}, {@code IF}, {@code DATE_FORMAT}).
     * <p>
     * <b>Example usage:</b>
     * <pre>{@code
     * SqlDialectPlugin plugin = registry.getPlugin("mysql", "8.0.35").orElseThrow();
     * DSL dsl = plugin.createDSL();
     * String sql = dsl.select("name").from("users").build();
     * }</pre>
     * <p>
     * <b>Thread Safety:</b> The thread safety of the returned DSL depends on the
     * {@code dslSupplier} implementation. It is recommended that the supplier creates
     * a new instance on each invocation to ensure thread safety.
     *
     * @return a {@link DSL} instance for this dialect, never {@code null}
     */
    public DSL createDSL() {
        return dslSupplier.get();
    }
}
