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
    void deleteWithSimpleWhere() {
        String result = dsl.deleteFrom("users").where("status").eq("inactive").build();

        assertThat(result)
                .contains("DELETE FROM")
                .contains("users")
                .contains("WHERE")
                .contains("status");
    }

    @Test
    void deleteWithMultipleConditions() {
        String result = dsl.deleteFrom("users")
                .where("status")
                .eq("inactive")
                .and("last_login")
                .lt("2023-01-01")
                .build();

        assertThat(result)
                .contains("DELETE FROM")
                .contains("users")
                .contains("WHERE")
                .contains("AND");
    }

    @Test
    void deleteWithOrCondition() {
        String result = dsl.deleteFrom("products")
                .where("stock")
                .eq(0)
                .or("discontinued")
                .eq(true)
                .build();

        assertThat(result)
                .contains("DELETE FROM")
                .contains("products")
                .contains("WHERE")
                .contains("OR");
    }

    @Test
    void deleteWithComplexConditions() {
        String result = dsl.deleteFrom("orders")
                .where("status")
                .eq("cancelled")
                .and("created_at")
                .lt("2023-01-01")
                .or("amount")
                .eq(0)
                .build();

        assertThat(result)
                .contains("DELETE FROM")
                .contains("orders")
                .contains("WHERE")
                .contains("status")
                .contains("cancelled")
                .contains("created_at")
                .contains("amount");
    }

    @Test
    void deleteWithoutWhere() {
        String result = dsl.deleteFrom("temp_table").build();

        assertThat(result).isEqualTo("DELETE FROM \"temp_table\"");
    }
}
