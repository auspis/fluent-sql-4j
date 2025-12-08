package lan.tlab.r4j.jdsql.plugin.util;

import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPlugin;

/**
 * Test helper for creating SQL dialect plugins in tests.
 * Provides factory methods to simplify plugin creation with common test defaults.
 */
public final class TestDialectPluginUtil {

    public static final String TEST_DIALECT = "test-dialect";
    public static final String OTHER_DIALECT = "other-dialect";
    public static final String BASE_VERSION = "3.4.5";

    private TestDialectPluginUtil() {}

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
        return create(TEST_DIALECT, dialectVersion, specFactory);
    }

    /**
     * Creates a plugin with default name, default version and custom spec factory.
     */
    public static SqlDialectPlugin create(PreparedStatementSpecFactory specFactory) {
        return create(TEST_DIALECT, "^" + BASE_VERSION, specFactory);
    }
}
