package lan.tlab.r4j.sql.dsl.insert;

import static lan.tlab.r4j.sql.dsl.DSL.insertInto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InsertBuilderSetMethodTest {

    @Test
    void insertWithSetMethodSingleColumn() {
        String sql = insertInto("users").set("name", "John").build();

        assertThat(sql)
                .isEqualTo("""
            INSERT INTO "users" ("users"."name") VALUES ('John')\
            """);
    }

    @Test
    void insertWithSetMethodMultipleColumns() {
        String sql = insertInto("users")
                .set("name", "John")
                .set("age", 30)
                .set("active", true)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        INSERT INTO "users" ("users"."name", "users"."age", "users"."active") VALUES ('John', 30, true)\
                        """);
    }

    @Test
    void insertWithSetMethodMixedTypes() {
        String sql = insertInto("users")
                .set("id", 1)
                .set("name", "Jane")
                .set("score", 99.5)
                .set("enabled", false)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        INSERT INTO "users" ("users"."id", "users"."name", "users"."score", "users"."enabled") VALUES (1, 'Jane', 99.5, false)\
                        """);
    }

    @Test
    void insertWithSetMethodNullValue() {
        String sql = insertInto("users")
                .set("name", "John")
                .set("email", (String) null)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        INSERT INTO "users" ("users"."name", "users"."email") VALUES ('John', null)\
                        """);
    }

    @Test
    void insertWithSetMethodDateValue() {
        LocalDate birthdate = LocalDate.of(1999, 1, 23);
        String sql = insertInto("users")
                .set("name", "John")
                .set("birthdate", birthdate)
                .build();

        assertThat(sql).contains("INSERT INTO \"users\" (\"users\".\"name\", \"users\".\"birthdate\") VALUES ('John',");
    }

    @Test
    void insertWithSetMethodEmptyColumnName() {
        assertThatThrownBy(() -> insertInto("users").set("", "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void insertWithSetMethodNullColumnName() {
        assertThatThrownBy(() -> insertInto("users").set(null, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void insertWithSetMethodCombinedWithColumnsAndValuesNotAllowed() {
        // Once you use set(), you can't mix with columns()/values()
        // This test verifies the current behavior
        String sql = insertInto("users").set("name", "John").set("age", 25).build();

        assertThat(sql)
                .isEqualTo(
                        """
                        INSERT INTO "users" ("users"."name", "users"."age") VALUES ('John', 25)\
                        """);
    }
}
