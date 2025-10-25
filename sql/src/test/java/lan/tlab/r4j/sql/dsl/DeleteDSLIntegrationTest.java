package lan.tlab.r4j.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import lan.tlab.r4j.sql.util.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class DeleteDSLIntegrationTest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void createsDeleteBuilderWithRenderer() {
        String result = dsl.deleteFrom("users").where("id").eq(1).build();

        assertThat(result).isEqualTo("""
                DELETE FROM "users" WHERE "users"."id" = 1""");
    }

    @Test
    void appliesRendererQuoting() {
        String result = dsl.deleteFrom("temp_table").build();

        assertThat(result).isEqualTo("""
                DELETE FROM "temp_table\"""");
    }

    @Test
    void fluentApiWithComplexConditions() {
        String result = dsl.deleteFrom("orders")
                .where("status")
                .eq("cancelled")
                .and("amount")
                .gt(100)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                DELETE FROM "orders" \
                WHERE ("orders"."status" = 'cancelled') \
                AND ("orders"."amount" > 100)""");
    }
}
