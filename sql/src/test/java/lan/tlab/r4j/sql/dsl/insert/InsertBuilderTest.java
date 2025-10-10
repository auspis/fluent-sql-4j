package lan.tlab.r4j.sql.dsl.insert;

import static lan.tlab.r4j.sql.dsl.DSL.insertInto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class InsertBuilderTest {

    @Test
    void insertWithDefaultValues() {
        String sql = insertInto("users").defaultValues().build();

        assertThat(sql).isEqualTo("""
            INSERT INTO "users" DEFAULT VALUES\
            """);
    }

    @Test
    void insertWithSingleColumnAndValue() {
        String sql = insertInto("users").set("name", "John").build();

        assertThat(sql)
                .isEqualTo("""
            INSERT INTO "users" ("users"."name") VALUES ('John')\
            """);
    }

    @Test
    void insertWithMultipleColumnsAndValues() {
        String sql = insertInto("users")
                .set("id", 1)
                .set("name", "John")
                .set("email", "john@example.com")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
            INSERT INTO "users" ("users"."id", "users"."name", "users"."email") VALUES (1, 'John', 'john@example.com')\
            """);
    }

    @Test
    void insertWithNullValue() {
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
    void insertWithBooleanValue() {
        String sql = insertInto("users").set("name", "John").set("active", true).build();

        assertThat(sql)
                .isEqualTo(
                        """
            INSERT INTO "users" ("users"."name", "users"."active") VALUES ('John', true)\
            """);
    }

    @Test
    void insertWithNumericValues() {
        String sql = insertInto("products")
                .set("id", 1)
                .set("price", 19.99)
                .set("quantity", 100)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        INSERT INTO "products" ("products"."id", "products"."price", "products"."quantity") VALUES (1, 19.99, 100)\
                        """);
    }

    @Test
    void invalidTableName() {
        assertThatThrownBy(() -> insertInto(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidNullTableName() {
        assertThatThrownBy(() -> insertInto(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidEmptyColumnName() {
        assertThatThrownBy(() -> insertInto("users").set("", "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void invalidNullColumnName() {
        assertThatThrownBy(() -> insertInto("users").set(null, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void insertWithMixedDataTypes() {
        String sql = insertInto("mixed_table")
                .set("text_col", "test")
                .set("int_col", 42)
                .set("bool_col", false)
                .set("null_col", (String) null)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        INSERT INTO "mixed_table" ("mixed_table"."text_col", "mixed_table"."int_col", "mixed_table"."bool_col", "mixed_table"."null_col") VALUES ('test', 42, false, null)\
                        """);
    }
}
