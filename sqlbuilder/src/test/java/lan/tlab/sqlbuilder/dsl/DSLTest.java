package lan.tlab.sqlbuilder.dsl;

import static lan.tlab.sqlbuilder.dsl.DSL.createTable;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DSLTest {

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
}
