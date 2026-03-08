package io.github.auspis.fluentsql4j.test.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class SqlCaptureHelperTest {

    @Test
    void constructorCreatesValidMocks() throws SQLException {
        SqlCaptureHelper helper = new SqlCaptureHelper();

        assertThat(helper.getConnection()).isNotNull();
        assertThat(helper.getPreparedStatement()).isNotNull();
    }

    @Test
    void getConnectionReturnsConsistentInstance() throws SQLException {
        SqlCaptureHelper helper = new SqlCaptureHelper();

        Connection conn1 = helper.getConnection();
        Connection conn2 = helper.getConnection();

        assertThat(conn1).isSameAs(conn2);
    }

    @Test
    void getPreparedStatementReturnsConsistentInstance() throws SQLException {
        SqlCaptureHelper helper = new SqlCaptureHelper();

        PreparedStatement ps1 = helper.getPreparedStatement();
        PreparedStatement ps2 = helper.getPreparedStatement();

        assertThat(ps1).isSameAs(ps2);
    }

    @Test
    void getSqlCaptureSqlFromPrepareStatement() throws SQLException {
        SqlCaptureHelper helper = new SqlCaptureHelper();
        String expectedSql = "SELECT \"name\" FROM \"users\"";

        helper.getConnection().prepareStatement(expectedSql);

        assertThat(helper.getSql()).isEqualTo(expectedSql);
    }

    @Test
    void getSqlCaptureSqlWithFlags() throws SQLException {
        SqlCaptureHelper helper = new SqlCaptureHelper();
        String expectedSql = "INSERT INTO \"users\" VALUES (?)";

        helper.getConnection().prepareStatement(expectedSql, java.sql.Statement.RETURN_GENERATED_KEYS);

        assertThat(helper.getSql()).isEqualTo(expectedSql);
    }

    @Test
    void multipleInstancesHaveIsolatedMocks() throws SQLException {
        SqlCaptureHelper helper1 = new SqlCaptureHelper();
        SqlCaptureHelper helper2 = new SqlCaptureHelper();

        helper1.getConnection().prepareStatement("SELECT 1");
        helper2.getConnection().prepareStatement("SELECT 2");

        assertThat(helper1.getSql()).isEqualTo("SELECT 1");
        assertThat(helper2.getSql()).isEqualTo("SELECT 2");
    }

    @Test
    void mockIsPreparedStatementFromPrepareStatement() throws SQLException {
        SqlCaptureHelper helper = new SqlCaptureHelper();
        String sql = "DELETE FROM \"users\"";

        PreparedStatement ps = helper.getConnection().prepareStatement(sql);

        assertThat(ps).isSameAs(helper.getPreparedStatement());
    }

    @Test
    void successiveCallsCaptureLastSql() throws SQLException {
        SqlCaptureHelper helper = new SqlCaptureHelper();

        helper.getConnection().prepareStatement("SELECT 1");
        helper.getConnection().prepareStatement("SELECT 2");

        assertThat(helper.getSql()).isEqualTo("SELECT 2");
    }
}
