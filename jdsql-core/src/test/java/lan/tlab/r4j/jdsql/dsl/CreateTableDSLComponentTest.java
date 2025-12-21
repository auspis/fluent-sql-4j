package lan.tlab.r4j.jdsql.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.plugin.util.StandardSqlUtil;
import lan.tlab.r4j.jdsql.test.util.annotation.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@ComponentTest
class CreateTableDSLComponentTest {

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
    void createsCreateTableBuilderWithPreparedStatementSpecFactory() throws SQLException {
        dsl.createTable("users")
                .column("id")
                .integer()
                .notNull()
                .column("name")
                .varchar(100)
                .primaryKey("id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                CREATE TABLE "users" (\
                "id" INTEGER NOT NULL, \
                "name" VARCHAR(100), \
                PRIMARY KEY ("id")\
                )""");
    }

    @Test
    void appliesPreparedStatementSpecFactoryQuoting() throws SQLException {
        dsl.createTable("temp_table").column("value").varchar(50).buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                CREATE TABLE "temp_table" ("value" VARCHAR(50))""");
    }

    @Test
    void fluentApiWithConstraints() throws SQLException {
        dsl.createTable("products")
                .column("id")
                .integer()
                .notNull()
                .column("sku")
                .varchar(50)
                .unique()
                .column("price")
                .decimal(10, 2)
                .primaryKey("id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                CREATE TABLE "products" (\
                "id" INTEGER NOT NULL, \
                "sku" VARCHAR(50), \
                "price" DECIMAL(10, 2), \
                PRIMARY KEY ("id"), \
                UNIQUE ("sku")\
                )""");
    }
}
