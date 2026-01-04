package io.github.massimiliano.fluentsql4j.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.massimiliano.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.massimiliano.fluentsql4j.test.util.annotation.ComponentTest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@ComponentTest
class DeleteDSLComponentTest {

    private DSL dsl;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        dsl = StandardSqlUtil.dsl();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
    }

    @Test
    void createsDeleteBuilderWithPreparedStatementSpecFactory() throws SQLException {
        dsl.deleteFrom("users").where().column("id").eq(1).build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                        DELETE FROM "users" WHERE "id" = ?""");
        verify(ps).setObject(1, 1);
    }

    @Test
    void appliesPreparedStatementSpecFactoryQuoting() throws SQLException {
        dsl.deleteFrom("temp_table").build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("DELETE FROM \"temp_table\"");
    }

    @Test
    void fluentApiWithComplexConditions() throws SQLException {
        dsl.deleteFrom("orders")
                .where()
                .column("status")
                .eq("cancelled")
                .and()
                .column("amount")
                .gt(100)
                .build(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                DELETE FROM "orders" \
                WHERE ("status" = ?) \
                AND ("amount" > ?)""");
        verify(ps).setObject(1, "cancelled");
        verify(ps).setObject(2, 100);
    }
}
