package io.github.auspis.fluentsql4j.test.helper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.mockito.ArgumentCaptor;

/**
 * Helper class for managing mocked JDBC connections in tests.
 *
 * <p>This class encapsulates the setup of mocked Connection, PreparedStatement, and
 * ArgumentCaptor, reducing boilerplate in test classes. It is meant to be instantiated in
 * each test class to maintain isolation between tests.
 *
 * <p>Unlike utility classes, this is a helper class that maintains state (the mock objects
 * and captured SQL) and is meant to be instantiated per test.
 *
 * <p>The constructor automatically creates and configures all mocks needed for testing SQL
 * builders. The captured SQL can be retrieved via {@link #getSql()}.
 *
 * <p>Example usage:
 *
 * <pre>
 * class MyBuilderTest {
 *     private MockedConnectionHelper mockHelper;
 *     private PreparedStatementSpecFactory specFactory;
 *
 *     &#64;BeforeEach
 *     void setUp() throws SQLException {
 *         mockHelper = new SqlCaptureHelper();
 *         specFactory = StandardSQLDialectPlugin.instance().createDSL().getSpecFactory();
 *     }
 *
 *     &#64;Test
 *     void myTest() throws SQLException {
 *         new SelectBuilder(specFactory, "name")
 *             .from("users")
 *             .build(mockHelper.getConnection());
 *
 *         assertThatSql(mockHelper.getSql()).isEqualTo("SELECT \"name\" FROM \"users\"");
 *     }
 * }
 * </pre>
 *
 */
public class SqlCaptureHelper {

    private final Connection connection;
    private final PreparedStatement preparedStatement;
    private final ArgumentCaptor<String> sqlCaptor;

    @SuppressWarnings("null")
    public SqlCaptureHelper() throws SQLException {
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(preparedStatement);
    }

    public Connection getConnection() {
        return connection;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public String getSql() {
        String value = sqlCaptor.getValue();
        if (value == null) {
            throw new IllegalStateException(
                    "No SQL was captured. Ensure build() was called with this helper's connection.");
        }
        return value;
    }
}
