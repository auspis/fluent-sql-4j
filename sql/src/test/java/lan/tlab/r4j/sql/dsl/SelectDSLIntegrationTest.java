package lan.tlab.r4j.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import lan.tlab.r4j.sql.util.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class SelectDSLIntegrationTest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void createsSelectBuilderWithRenderer() {
        String result = dsl.select("name", "email").from("users").build();

        assertThat(result).isEqualTo("""
                SELECT "users"."name", "users"."email" FROM "users\"""");
    }

    @Test
    void appliesRendererQuoting() {
        String result = dsl.select("id", "value").from("temp_table").build();

        assertThat(result)
                .isEqualTo("""
                SELECT "temp_table"."id", "temp_table"."value" FROM "temp_table\"""");
    }

    @Test
    void fluentApiWithComplexQuery() {
        String result = dsl.select("name", "age")
                .from("users")
                .where("age")
                .gt(18)
                .and("active")
                .eq(true)
                .orderBy("name")
                .fetch(10)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "users"."name", "users"."age" \
                FROM "users" \
                WHERE ("users"."age" > 18) AND ("users"."active" = true) \
                ORDER BY "users"."name" ASC \
                OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY""");
    }
}
