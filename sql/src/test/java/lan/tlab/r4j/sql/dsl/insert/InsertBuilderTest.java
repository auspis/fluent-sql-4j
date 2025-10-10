package lan.tlab.r4j.sql.dsl.insert;

import static lan.tlab.r4j.sql.dsl.DSL.insertInto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
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
        String sql = insertInto("users").columns("name").values("John").build();

        assertThat(sql)
                .isEqualTo("""
            INSERT INTO "users" ("users"."name") VALUES ('John')\
            """);
    }

    @Test
    void insertWithMultipleColumnsAndValues() {
        String sql = insertInto("users")
                .columns("id", "name", "email")
                .values(Literal.of(1), Literal.of("John"), Literal.of("john@example.com"))
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
                .columns("name", "email")
                .values("John", null)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
            INSERT INTO "users" ("users"."name", "users"."email") VALUES ('John', null)\
            """);
    }

    @Test
    void insertWithBooleanValue() {
        String sql = insertInto("users")
                .columns("name", "active")
                .values(Literal.of("John"), Literal.of(true))
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
            INSERT INTO "users" ("users"."name", "users"."active") VALUES ('John', true)\
            """);
    }

    @Test
    void insertWithNumericValues() {
        String sql = insertInto("products")
                .columns("id", "price", "quantity")
                .values(1, 19.99, 100)
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
    void invalidEmptyColumns() {
        assertThatThrownBy(() -> insertInto("users").columns())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one column must be specified");
    }

    @Test
    void invalidNullColumns() {
        assertThatThrownBy(() -> insertInto("users").columns((String[]) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one column must be specified");
    }

    @Test
    void invalidEmptyColumnName() {
        assertThatThrownBy(() -> insertInto("users").columns("name", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void invalidEmptyValues() {
        assertThatThrownBy(() -> insertInto("users").columns("name").values(new String[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one value must be specified");
    }

    @Test
    void invalidNullValues() {
        assertThatThrownBy(() -> insertInto("users").columns("name").values((String[]) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one value must be specified");
    }

    @Test
    void columnsWithoutValues() {
        assertThatThrownBy(() -> insertInto("users").columns("name").build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Columns specified but no values provided");
    }

    @Test
    void mismatchedColumnsAndValues() {
        assertThatThrownBy(() -> insertInto("users")
                        .columns("name", "email")
                        .values("John")
                        .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Number of columns")
                .hasMessageContaining("does not match number of values");
    }

    @Test
    void insertWithMixedDataTypes() {
        String sql = insertInto("mixed_table")
                .columns("text_col", "int_col", "bool_col", "null_col")
                .values(Literal.of("test"), Literal.of(42), Literal.of(false), Literal.ofNull())
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        INSERT INTO "mixed_table" ("mixed_table"."text_col", "mixed_table"."int_col", "mixed_table"."bool_col", "mixed_table"."null_col") VALUES ('test', 42, false, null)\
                        """);
    }
}
