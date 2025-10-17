package lan.tlab.r4j.sql.plugin;

import java.util.Objects;
import java.util.function.Supplier;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.util.SemVerUtil;

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
 * Plugins declare version support using Semantic Versioning (SemVer) notation.
 * The registry uses this information to match plugins to requested database versions.
 * Multiple plugins can be registered for the same dialect with different version ranges.
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * var plugin = new SqlDialectPlugin(
 *     "mysql",
 *     "^8.0.0",  // Supports all MySQL 8.x versions
 *     MySqlRenderer::new
 * );
 * }</pre>
 * <p>
 * <b>Supported version formats:</b>
 * <ul>
 *   <li>Exact version: {@code "8.0.35"}</li>
 *   <li>Caret range (compatible): {@code "^8.0.0"} (matches {@code >=8.0.0 <9.0.0})</li>
 *   <li>Tilde range (patch): {@code "~5.7.42"} (matches {@code >=5.7.42 <5.8.0})</li>
 *   <li>Explicit range: {@code ">=8.0.0 <9.0.0"}</li>
 *   <li>Compound conditions: {@code ">=5.7.0 <8.0.0 || >=8.0.20"}</li>
 * </ul>
 *
 * @param dialectName the canonical name of the SQL dialect in lowercase (e.g., "mysql", "postgresql")
 * @param dialectVersion the SemVer version range this plugin supports (e.g., "^8.0.0")
 * @param rendererSupplier a supplier that creates new {@link SqlRenderer} instances
 * @see SqlDialectPluginProvider
 * @see SqlDialectRegistry
 * @see <a href="https://semver.org/">Semantic Versioning</a>
 * @see <a href="https://github.com/npm/node-semver">NPM semver ranges</a>
 * @since 1.0
 */
public record SqlDialectPlugin(String dialectName, String dialectVersion, Supplier<SqlRenderer> rendererSupplier) {

    /**
     * Compact constructor with validation.
     * <p>
     * Validates that all parameters are non-null and that the dialect version
     * is a valid SemVer range. This ensures that invalid plugins cannot be constructed.
     *
     * @throws NullPointerException if any parameter is {@code null}
     * @throws IllegalArgumentException if {@code dialectVersion} is not a valid SemVer range
     */
    public SqlDialectPlugin {
        Objects.requireNonNull(dialectName, "Dialect name must not be null");
        Objects.requireNonNull(dialectVersion, "Dialect version must not be null");
        Objects.requireNonNull(rendererSupplier, "Renderer supplier must not be null");

        if (!SemVerUtil.isValidRange(dialectVersion)) {
            throw new IllegalArgumentException("Invalid version range '" + dialectVersion + "' in plugin '"
                    + dialectName + "'. Must be a valid SemVer range (e.g., '^8.0.0', '~5.7.0', '>=14.0.0 <15.0.0')");
        }
    }

    /**
     * Creates a new {@link SqlRenderer} configured for this SQL dialect.
     * <p>
     * The renderer is responsible for converting the abstract syntax tree (AST) representation
     * of SQL statements into dialect-specific SQL text. Each invocation returns a new
     * instance to ensure thread safety.
     *
     * @return a new, fully configured {@link SqlRenderer} instance, never {@code null}
     */
    public SqlRenderer createRenderer() {
        return rendererSupplier.get();
    }
}
