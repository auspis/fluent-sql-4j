package io.github.massimiliano.fluentsql4j.plugin.util;

import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.dsl.DSL;
import io.github.massimiliano.fluentsql4j.plugin.SqlDialectPlugin;

/**
 * Test util for creating SQL dialect plugins in tests.
 * Provides factory methods to simplify plugin creation with common test defaults.
 */
public final class SqlDialectPluginUtil {

    private SqlDialectPluginUtil() {}

    /**
     * Creates a plugin with custom name, version and renderer.
     */
    public static SqlDialectPlugin create(
            String dialectName, String dialectVersion, PreparedStatementSpecFactory specFactory) {
        return new SqlDialectPlugin(dialectName, dialectVersion, () -> new DSL(specFactory));
    }

    /**
     * Creates a plugin with default name, custom version and spec factory.
     */
    public static SqlDialectPlugin create(String dialectVersion, PreparedStatementSpecFactory specFactory) {
        return create(TestDialectPlugin.DIALECT_NAME, dialectVersion, specFactory);
    }

    /**
     * Creates a plugin with default name, default version and custom spec factory.
     */
    public static SqlDialectPlugin create(PreparedStatementSpecFactory specFactory) {
        return create(TestDialectPlugin.DIALECT_NAME, "^" + TestDialectPlugin.DIALECT_VERSION, specFactory);
    }
}
