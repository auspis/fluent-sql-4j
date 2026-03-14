package io.github.auspis.fluentsql4j.dsl.truncate;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TruncateBuilderTest {

    private static SqlCaptureHelper sqlCaptureHelper;
    private static PreparedStatementSpecFactory specFactory;

    @BeforeAll
    static void beforeAll() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = new PreparedStatementSpecFactory(new AstToPreparedStatementSpecVisitor());
    }

    @Test
    void ok() throws SQLException {
        new TruncateBuilder(specFactory, "users").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("TRUNCATE TABLE \"users\"");
    }

    @Test
    void invalidTableName() {
        assertThatThrownBy(() -> new TruncateBuilder(specFactory, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }
}
