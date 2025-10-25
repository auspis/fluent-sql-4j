package lan.tlab.r4j.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
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
    void insertWithDefaultValues() {
        String sql = dsl.insertInto("users").defaultValues().build();

        assertThat(sql).contains("INSERT INTO").contains("users").contains("DEFAULT VALUES");
    }

    @Test
    void insertWithSingleColumn() {
        String sql = dsl.insertInto("users").set("name", "John").build();

        assertThat(sql)
                .contains("INSERT INTO")
                .contains("users")
                .contains("name")
                .contains("VALUES");
    }

    @Test
    void insertWithMultipleColumns() {
        String sql = dsl.insertInto("users")
                .set("id", 1)
                .set("name", "John")
                .set("email", "john@example.com")
                .build();

        assertThat(sql)
                .contains("INSERT INTO")
                .contains("users")
                .contains("id")
                .contains("name")
                .contains("email")
                .contains("VALUES");
    }

    @Test
    void insertWithMixedTypes() {
        String sql = dsl.insertInto("users")
                .set("name", "Alice")
                .set("age", 30)
                .set("active", true)
                .set("salary", 50000.50)
                .build();

        assertThat(sql)
                .contains("INSERT INTO")
                .contains("users")
                .contains("name")
                .contains("age")
                .contains("active")
                .contains("salary")
                .contains("VALUES");
    }

    @Test
    void insertWithDate() {
        LocalDate birthdate = LocalDate.of(1990, 1, 15);
        String sql = dsl.insertInto("users")
                .set("name", "Bob")
                .set("birthdate", birthdate)
                .build();

        assertThat(sql)
                .contains("INSERT INTO")
                .contains("users")
                .contains("birthdate")
                .contains("VALUES");
    }
}
