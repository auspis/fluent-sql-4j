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
    void updateWithSingleSet() {
        String result =
                dsl.update("users").set("name", "John").where("id").eq(1).build();

        assertThat(result)
                .contains("UPDATE")
                .contains("users")
                .contains("SET")
                .contains("name")
                .contains("WHERE");
    }

    @Test
    void updateWithMultipleSets() {
        String result = dsl.update("users")
                .set("name", "John")
                .set("age", 30)
                .where("id")
                .eq(1)
                .build();

        assertThat(result)
                .contains("UPDATE")
                .contains("users")
                .contains("SET")
                .contains("name")
                .contains("age")
                .contains("WHERE");
    }

    @Test
    void updateWithoutWhere() {
        String result = dsl.update("users").set("status", "active").build();

        assertThat(result).contains("UPDATE").contains("users").contains("SET").contains("status");
    }

    @Test
    void updateWithComplexWhere() {
        String result = dsl.update("products")
                .set("stock", 0)
                .where("discontinued")
                .eq(true)
                .and("last_order_date")
                .lt("2023-01-01")
                .build();

        assertThat(result)
                .contains("UPDATE")
                .contains("products")
                .contains("SET")
                .contains("stock")
                .contains("WHERE")
                .contains("AND");
    }
}
