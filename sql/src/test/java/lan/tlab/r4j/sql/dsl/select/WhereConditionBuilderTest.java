package lan.tlab.r4j.sql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.sql.dsl.DSL;
import org.junit.jupiter.api.Test;

class WhereConditionBuilderTest {

    @Test
    void stringComparisons() {
        // Test string equality
        String sql = DSL.select("name").from("users").where("name").eq("John").build();
        assertThat(sql).contains("WHERE").contains("name").contains("= 'John'");

        // Test string inequality
        sql = DSL.select("name").from("users").where("name").ne("Jane").build();
        assertThat(sql).contains("WHERE").contains("name").contains("!= 'Jane'");

        // Test string comparisons
        sql = DSL.select("name").from("users").where("name").gt("A").build();
        assertThat(sql).contains("WHERE").contains("name").contains("> 'A'");

        sql = DSL.select("name").from("users").where("name").lt("Z").build();
        assertThat(sql).contains("WHERE").contains("name").contains("< 'Z'");

        sql = DSL.select("name").from("users").where("name").gte("B").build();
        assertThat(sql).contains("WHERE").contains("name").contains(">= 'B'");

        sql = DSL.select("name").from("users").where("name").lte("Y").build();
        assertThat(sql).contains("WHERE").contains("name").contains("<= 'Y'");
    }

    @Test
    void numberComparisons() {
        // Test integer operations
        String sql = DSL.select("age").from("users").where("age").eq(25).build();
        assertThat(sql).contains("WHERE").contains("age").contains("= 25");

        sql = DSL.select("age").from("users").where("age").ne(30).build();
        assertThat(sql).contains("WHERE").contains("age").contains("!= 30");

        sql = DSL.select("age").from("users").where("age").gt(18).build();
        assertThat(sql).contains("WHERE").contains("age").contains("> 18");

        sql = DSL.select("age").from("users").where("age").lt(65).build();
        assertThat(sql).contains("WHERE").contains("age").contains("< 65");

        // Test double operations
        sql = DSL.select("salary")
                .from("employees")
                .where("salary")
                .gte(50000.5)
                .build();
        assertThat(sql).contains("WHERE").contains("salary").contains(">= 50000.5");

        sql = DSL.select("salary")
                .from("employees")
                .where("salary")
                .lte(100000.75)
                .build();
        assertThat(sql).contains("WHERE").contains("salary").contains("<= 100000.75");
    }

    @Test
    void booleanComparisons() {
        // Test boolean equality
        String sql = DSL.select("active").from("users").where("active").eq(true).build();
        assertThat(sql).contains("WHERE").contains("active").contains("= true");

        // Test boolean inequality
        sql = DSL.select("active").from("users").where("active").ne(false).build();
        assertThat(sql).contains("WHERE").contains("active").contains("!= false");
    }

    @Test
    void localDateComparisons() {
        LocalDate date = LocalDate.of(2024, 3, 15);

        // Test LocalDate operations
        String sql = DSL.select("birth_date")
                .from("users")
                .where("birth_date")
                .eq(date)
                .build();
        assertThat(sql).contains("WHERE").contains("birth_date").contains("= '2024-03-15'");

        sql = DSL.select("birth_date")
                .from("users")
                .where("birth_date")
                .gt(date)
                .build();
        assertThat(sql).contains("WHERE").contains("birth_date").contains("> '2024-03-15'");

        sql = DSL.select("birth_date")
                .from("users")
                .where("birth_date")
                .lt(date)
                .build();
        assertThat(sql).contains("WHERE").contains("birth_date").contains("< '2024-03-15'");
    }

    @Test
    void localDateTimeComparisons() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 15, 10, 30, 45);

        // Test LocalDateTime operations
        String sql = DSL.select("created_at")
                .from("posts")
                .where("created_at")
                .eq(dateTime)
                .build();
        assertThat(sql).contains("WHERE").contains("created_at").contains("= '2024-03-15T10:30:45'");

        sql = DSL.select("created_at")
                .from("posts")
                .where("created_at")
                .gte(dateTime)
                .build();
        assertThat(sql).contains("WHERE").contains("created_at").contains(">= '2024-03-15T10:30:45'");
    }

    @Test
    void stringSpecialOperations() {
        // Test LIKE operation
        String sql =
                DSL.select("name").from("users").where("name").like("John%").build();
        assertThat(sql).contains("WHERE").contains("name").contains("LIKE 'John%'");

        sql = DSL.select("email")
                .from("users")
                .where("email")
                .like("%@example.com")
                .build();
        assertThat(sql).contains("WHERE").contains("email").contains("LIKE '%@example.com'");
    }

    @Test
    void nullChecks() {
        // Test IS NULL
        String sql = DSL.select("email").from("users").where("email").isNull().build();
        assertThat(sql).contains("WHERE").contains("email").contains("IS NULL");

        // Test IS NOT NULL
        sql = DSL.select("email").from("users").where("email").isNotNull().build();
        assertThat(sql).contains("WHERE").contains("email").contains("IS NOT NULL");
    }

    @Test
    void betweenConvenienceMethods() {
        // Test LocalDate between
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String sql = DSL.select("birth_date")
                .from("users")
                .where("birth_date")
                .between(startDate, endDate)
                .build();
        assertThat(sql)
                .contains("WHERE")
                .contains("birth_date")
                .contains(">= '2024-01-01'")
                .contains("AND")
                .contains("<= '2024-12-31'");

        // Test LocalDateTime between
        LocalDateTime startDateTime = LocalDateTime.of(2024, 3, 1, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 3, 31, 23, 59, 59);
        sql = DSL.select("created_at")
                .from("posts")
                .where("created_at")
                .between(startDateTime, endDateTime)
                .build();
        assertThat(sql)
                .contains("WHERE")
                .contains("created_at")
                .contains(">= '2024-03-01T00:00'")
                .contains("AND")
                .contains("<= '2024-03-31T23:59:59'");

        // Test Number between
        sql = DSL.select("age").from("users").where("age").between(18, 65).build();
        assertThat(sql)
                .contains("WHERE")
                .contains("age")
                .contains(">= 18")
                .contains("AND")
                .contains("<= 65");
    }

    @Test
    void logicalOperators() {
        // Test AND with different types
        String sql = DSL.select("name", "age")
                .from("users")
                .where("name")
                .eq("John")
                .and("age")
                .gt(25)
                .build();
        assertThat(sql)
                .contains("WHERE")
                .contains("name")
                .contains("= 'John'")
                .contains("AND")
                .contains("age")
                .contains("> 25");

        // Test OR with different types
        sql = DSL.select("name", "age")
                .from("users")
                .where("age")
                .lt(18)
                .or("active")
                .eq(false)
                .build();
        assertThat(sql)
                .contains("WHERE")
                .contains("age")
                .contains("< 18")
                .contains("OR")
                .contains("active")
                .contains("= false");
    }

    @Test
    void tableAliasSupport() {
        // Test with table alias
        String sql = DSL.select("name")
                .from("users")
                .as("u")
                .where("name")
                .eq("John")
                .build();
        assertThat(sql).contains("WHERE").contains("name").contains("= 'John'");
        assertThat(sql).contains("FROM").contains("users").contains("AS u");

        // Verify that column references use the alias
        assertThat(sql).contains("\"u\".\"name\"");
    }
}
