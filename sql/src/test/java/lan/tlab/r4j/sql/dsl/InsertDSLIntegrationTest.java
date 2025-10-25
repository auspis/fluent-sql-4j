package lan.tlab.r4j.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import lan.tlab.r4j.sql.util.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class InsertDSLIntegrationTest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void createsInsertBuilderWithRenderer() {
        String result = dsl.insertInto("users").set("name", "John").build();

        assertThat(result).isEqualTo("""
                INSERT INTO "users" ("users"."name") VALUES ('John')""");
    }

    @Test
    void appliesRendererQuoting() {
        String result = dsl.insertInto("temp_table").defaultValues().build();

        assertThat(result).isEqualTo("""
                INSERT INTO "temp_table" DEFAULT VALUES""");
    }

    @Test
    void fluentApiWithMultipleColumns() {
        String result = dsl.insertInto("products")
                .set("id", 1)
                .set("name", "Widget")
                .set("price", 19.99)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                INSERT INTO "products" ("products"."id", "products"."name", "products"."price") VALUES (1, 'Widget', 19.99)""");
    }
}
