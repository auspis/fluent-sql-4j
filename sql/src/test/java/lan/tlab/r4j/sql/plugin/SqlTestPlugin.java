package lan.tlab.r4j.sql.plugin;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

/**
 * Test helper for creating SQL dialect plugins in tests.
 * Provides factory methods to simplify plugin creation with common test defaults.
 */
public final class SqlTestPlugin {

    public static final String TEST_DIALECT = "test-dialect";
    public static final String OTHER_DIALECT = "other-dialect";
    public static final String BASE_VERSION = "3.4.5";

    private SqlTestPlugin() {}

    /**
     * Creates a plugin with custom name, version and renderer.
     */
    public static SqlDialectPlugin create(String dialectName, String dialectVersion, SqlRenderer renderer) {
        return new SqlDialectPlugin(dialectName, dialectVersion, () -> renderer);
    }

    /**
     * Creates a plugin with default name, custom version and renderer.
     */
    public static SqlDialectPlugin create(String dialectVersion, SqlRenderer renderer) {
        return create(TEST_DIALECT, dialectVersion, renderer);
    }

    /**
     * Creates a plugin with default name, default version and custom renderer.
     */
    public static SqlDialectPlugin create(SqlRenderer renderer) {
        return create(TEST_DIALECT, "^" + BASE_VERSION, renderer);
    }
}
