package lan.tlab.r4j.jdsql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import lan.tlab.r4j.jdsql.test.util.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class DeleteDSLIntegrationTest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = StandardSqlRendererFactory.dslStandardSql();
    }

    @Test
    void createsDeleteBuilderWithRenderer() {
        String result = dsl.deleteFrom("users").where().column("id").eq(1).build();

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
                .where()
                .column("status")
                .eq("cancelled")
                .and()
                .column("amount")
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
