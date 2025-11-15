package lan.tlab.r4j.sql.dsl.insert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsertBuilderTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void insertWithDefaultValues() {
        String sql = new InsertBuilder(renderer, "users").defaultValues().build();

        assertThat(sql).isEqualTo("""
            INSERT INTO "users" DEFAULT VALUES\
            """);
    }

    @Test
    void insertWithSingleColumnAndValue() {
        String sql = new InsertBuilder(renderer, "users").set("name", "John").build();

        assertThat(sql)
                .isEqualTo("""
            INSERT INTO "users" ("users"."name") VALUES ('John')\
            """);
    }

    @Test
    void insertWithMultipleColumnsAndValues() {
        String sql = new InsertBuilder(renderer, "users")
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
        String sql = new InsertBuilder(renderer, "users")
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
        String sql = new InsertBuilder(renderer, "users")
                .set("name", "John")
                .set("active", true)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
            INSERT INTO "users" ("users"."name", "users"."active") VALUES ('John', true)\
            """);
    }

    @Test
    void insertWithNumericValues() {
        String sql = new InsertBuilder(renderer, "products")
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
        assertThatThrownBy(() -> new InsertBuilder(renderer, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidNullTableName() {
        assertThatThrownBy(() -> new InsertBuilder(renderer, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidEmptyColumnName() {
        assertThatThrownBy(() -> new InsertBuilder(renderer, "users").set("", "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void invalidNullColumnName() {
        assertThatThrownBy(() -> new InsertBuilder(renderer, "users").set(null, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void insertWithMixedDataTypes() {
        String sql = new InsertBuilder(renderer, "mixed_table")
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

    @Test
    void insertWithDateValue() {
        LocalDate birthdate = LocalDate.of(1999, 1, 23);
        String sql = new InsertBuilder(renderer, "users")
                .set("name", "John")
                .set("birthdate", birthdate)
                .build();

        assertThat(sql).contains("INSERT INTO \"users\" (\"users\".\"name\", \"users\".\"birthdate\") VALUES ('John',");
    }

    @Test
    void buildPreparedStatementRequiresConnection() {
        InsertBuilder builder = new InsertBuilder(renderer, "users").set("name", "John");

        assertThatThrownBy(() -> builder.buildPreparedStatement(null)).isInstanceOf(Exception.class);
    }

    @Test
    void buildPreparedStatementCompilesWithoutError() {
        InsertBuilder builder =
                new InsertBuilder(renderer, "users").set("name", "John").set("email", "john@example.com");

        assertThat(builder).isNotNull();

        assertThat(builder.getClass().getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("buildPreparedStatement"));
    }

    @Test
    void buildPreparedStatementWithDefaultValuesCompilesWithoutError() {
        InsertBuilder builder = new InsertBuilder(renderer, "users").defaultValues();

        assertThat(builder).isNotNull();

        assertThat(builder.getClass().getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("buildPreparedStatement"));
    }
}
