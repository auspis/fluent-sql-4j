package lan.tlab.sqlbuilder.dsl;

import static lan.tlab.sqlbuilder.dsl.DSL.createTable;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TableBuilderTest {

    @Test
    void createUserTable() {
        String sql = createTable("User")
                .column("id")
                .integer()
                .primaryKey()
                .notNull()
                .column("name")
                .varchar(100)
                .notNull()
                .column("email")
                .varchar(255)
                .column("birthdate")
                .date()
                .column("score")
                .decimal(10, 2)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                    CREATE TABLE "User" (\
                    "id" INTEGER NOT NULL, \
                    "name" VARCHAR(100) NOT NULL, \
                    "email" VARCHAR(255), \
                    "birthdate" DATE, \
                    "score" DECIMAL(10, 2), \
                    PRIMARY KEY ("id")\
                    )""");
    }

    @Test
    void columnIntegerPrimaryKey() {
        String sqlShortForm = createTable("Test").columnIntegerPrimaryKey("id").build();
        assertThat(sqlShortForm).contains("\"id\" INTEGER NOT NULL").contains("PRIMARY KEY (\"id\")");

        String sqlLongForm = createTable("Test")
                .column("id")
                .integer()
                .primaryKey()
                .notNull()
                .build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnStringPrimaryKey() {
        String sqlShortForm =
                createTable("Test").columnStringPrimaryKey("code", 50).build();

        assertThat(sqlShortForm).contains("\"code\" VARCHAR(50) NOT NULL").contains("PRIMARY KEY (\"code\")");

        String sqlLongForm = createTable("Test")
                .column("code")
                .varchar(50)
                .primaryKey()
                .notNull()
                .build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnTimestampNotNull() {
        String sqlShortForm =
                createTable("Test").columnTimestampNotNull("created_at").build();

        assertThat(sqlShortForm).contains("\"created_at\" TIMESTAMP NOT NULL");

        String sqlLongForm =
                createTable("Test").column("created_at").timestamp().notNull().build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnVarcharNotNull() {
        String sqlShortForm =
                createTable("Test").columnVarcharNotNull("name", 100).build();

        assertThat(sqlShortForm).contains("\"name\" VARCHAR(100) NOT NULL");

        String sqlLongForm =
                createTable("Test").column("name").varchar(100).notNull().build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnDecimalNotNull() {
        String sqlShortForm =
                createTable("Test").columnDecimalNotNull("price", 10, 2).build();

        assertThat(sqlShortForm).contains("\"price\" DECIMAL(10, 2) NOT NULL");

        String sqlLongForm =
                createTable("Test").column("price").decimal(10, 2).notNull().build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void allConvenienceMethodsTogether() {
        String sql = createTable("Product")
                .columnIntegerPrimaryKey("id")
                .columnStringPrimaryKey("sku", 20) // This would be a second primary key
                .columnVarcharNotNull("name", 100)
                .columnDecimalNotNull("price", 10, 2)
                .columnTimestampNotNull("created_at")
                .build();

        assertThat(sql)
                .contains("\"id\" INTEGER NOT NULL")
                .contains("\"sku\" VARCHAR(20) NOT NULL")
                .contains("\"name\" VARCHAR(100) NOT NULL")
                .contains("\"price\" DECIMAL(10, 2) NOT NULL")
                .contains("\"created_at\" TIMESTAMP NOT NULL")
                .contains("PRIMARY KEY (\"id\", \"sku\")"); // Composite primary key
    }
}
