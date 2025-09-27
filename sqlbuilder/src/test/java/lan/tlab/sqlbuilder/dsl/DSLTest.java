package lan.tlab.sqlbuilder.dsl;

import static lan.tlab.sqlbuilder.dsl.DSL.*;
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
                .isEqualTo("CREATE TABLE User (" + "id INTEGER PRIMARY KEY NOT NULL, "
                        + "name VARCHAR(100) NOT NULL, "
                        + "email VARCHAR(255), "
                        + "birthdate DATE, "
                        + "score DECIMAL(10, 2)"
                        + ")");
    }
}
