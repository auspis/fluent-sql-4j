package lan.tlab.r4j.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import lan.tlab.r4j.sql.util.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class UpdateDSLIntegrationTest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void createsUpdateBuilderWithRenderer() {
        String result =
                dsl.update("users").set("name", "John").where("id").eq(1).build();

        assertThat(result).isEqualTo("""
                UPDATE "users" SET "name" = 'John' WHERE "users"."id" = 1""");
    }

    @Test
    void appliesRendererQuoting() {
        String result = dsl.update("temp_table").set("status", "active").build();

        assertThat(result).isEqualTo("""
                UPDATE "temp_table" SET "status" = 'active'""");
    }

    @Test
    void fluentApiWithComplexConditions() {
        String result = dsl.update("products")
                .set("stock", 0)
                .set("discontinued", true)
                .where("last_order_date")
                .lt("2023-01-01")
                .and("quantity")
                .eq(0)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                UPDATE "products" SET "stock" = 0, "discontinued" = true WHERE ("products"."last_order_date" < '2023-01-01') AND ("products"."quantity" = 0)""");
    }
}
