package lan.tlab.r4j.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import lan.tlab.r4j.sql.util.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class CreateTableDSLIntegrationTest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void createsCreateTableBuilderWithRenderer() {
        String result = dsl.createTable("users")
                .column("id")
                .integer()
                .notNull()
                .column("name")
                .varchar(100)
                .primaryKey("id")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                CREATE TABLE "users" (\
                "id" INTEGER NOT NULL, \
                "name" VARCHAR(100), \
                PRIMARY KEY ("id")\
                )""");
    }

    @Test
    void appliesRendererQuoting() {
        String result =
                dsl.createTable("temp_table").column("value").varchar(50).build();

        assertThat(result).isEqualTo("""
                CREATE TABLE "temp_table" ("value" VARCHAR(50))""");
    }

    @Test
    void fluentApiWithConstraints() {
        String result = dsl.createTable("products")
                .column("id")
                .integer()
                .notNull()
                .column("sku")
                .varchar(50)
                .unique()
                .column("price")
                .decimal(10, 2)
                .primaryKey("id")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                CREATE TABLE "products" (\
                "id" INTEGER NOT NULL, \
                "sku" VARCHAR(50), \
                "price" DECIMAL(10, 2), \
                PRIMARY KEY ("id"), \
                UNIQUE ("sku")\
                )""");
    }
}
