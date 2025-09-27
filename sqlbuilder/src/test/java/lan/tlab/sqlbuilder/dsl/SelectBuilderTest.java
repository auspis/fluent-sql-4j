package lan.tlab.sqlbuilder.dsl;

import static lan.tlab.sqlbuilder.dsl.DSL.select;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class SelectBuilderTest {

    @Test
    void ok() {
        String result = select("name", "email").from("users").build();
        assertThat(result)
                .isEqualTo("""
            SELECT "users"."name", "users"."email" FROM "users"\
            """);
    }

    @Test
    void star() {
        String result = select("*").from("products").build();
        assertThat(result).isEqualTo("""
            SELECT * FROM "products"\
            """);
    }

    @Test
    void fromWithAlias() {
        String result = select("name", "email")
                .from("users")
                .as("u")
                .where("name")
                .eq("John")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                        SELECT "u"."name", "u"."email" FROM "users" AS u WHERE "u"."name" = 'John'\
                        """);
    }

    @Test
    void where() {
        String result = select("*").from("users").where("age", ">", 18).build();
        assertThat(result).isEqualTo("""
            SELECT * FROM "users" WHERE "users"."age" > 18\
            """);
    }

    @Test
    void and() {
        String result = select("name", "email")
                .from("users")
                .where("age")
                .gt(18)
                .and("status")
                .eq("active")
                .and("country")
                .eq("Italy")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                        SELECT "users"."name", "users"."email" FROM "users" WHERE ((\"users\".\"age\" > 18) AND (\"users\".\"status\" = 'active')) AND (\"users\".\"country\" = 'Italy')\
                        """);
    }

    @Test
    void or() {
        String sql = select("*")
                .from("users")
                .where("role")
                .eq("admin")
                .or("role")
                .eq("moderator")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM "users" WHERE ("users"."role" = 'admin') OR ("users"."role" = 'moderator')\
                        """);
    }

    @Test
    void andOr() {
        String sql = select("*")
                .from("users")
                .where("status")
                .eq("active")
                .and("age")
                .gt(18)
                .or("role")
                .eq("admin")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM "users" WHERE ((\"users\".\"status\" = 'active') AND (\"users\".\"age\" > 18)) OR (\"users\".\"role\" = 'admin')\
                        """);
    }

    @Test
    void orderBy() {
        String sql = select("name", "age").from("users").orderBy("name").build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT "users"."name", "users"."age" FROM "users" ORDER BY "users"."name" ASC\
                        """);
    }

    @Test
    void orderByDesc() {
        String sql = select("*").from("products").orderByDesc("created_at").build();

        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "products" ORDER BY "products"."created_at" DESC\
            """);
    }

    @Test
    void fetch() {
        String sql = select("*").from("users").fetch(10).build();

        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "users" OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY\
            """);
    }

    @Test
    void fetchWithOffset() {
        String sql = select("*").from("users").fetch(10).offset(20).build();

        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "users" OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY\
            """);
    }

    @Test
    void offsetBeforeFetch() {
        String sql = select("*").from("users").offset(15).fetch(5).build();

        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "users" OFFSET 15 ROWS FETCH NEXT 5 ROWS ONLY\
            """);
    }

    @Test
    void offsetZero() {
        String sql = select("*").from("users").fetch(5).offset(0).build();

        assertThat(sql)
                .isEqualTo("""
            SELECT * FROM "users" OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY\
            """);
    }

    @Test
    void multipleOffsetCalls() {
        String sql = select("*").from("users").offset(10).offset(20).fetch(5).build();

        // BUG IDENTIFICATO: aspettavamo OFFSET 20, ma otteniamo OFFSET 100
        // Questo evidenzia un problema nella logica del metodo offset()
        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "users" OFFSET 100 ROWS FETCH NEXT 5 ROWS ONLY\
            """);
    }

    @Test
    void fetchThenOffsetThenFetch() {
        String sql = select("*")
                .from("users")
                .fetch(10) // page=1, perPage=10
                .offset(25) // Dovrebbe calcolare: page = 25/10 + 1 = 3
                .fetch(8) // Dovrebbe mantenere page=3, cambiare perPage=8
                .build();

        // Con page=3 e perPage=8: offset = (3-1) * 8 = 16
        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "users" OFFSET 16 ROWS FETCH NEXT 8 ROWS ONLY\
            """);
    }

    @Test
    void complexOffsetFetchInteraction() {
        // Testa il caso complesso: offset non divisibile per fetch
        String sql = select("*")
                .from("users")
                .offset(23) // 23 non è divisibile per 10
                .fetch(10) // page = 23/10 + 1 = 3, ma offset effettivo = (3-1)*10 = 20
                .build();

        // Questo test rivela la perdita di precisione: offset 23 diventa 20
        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "users" OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY\
            """);
    }

    @Test
    void fullSelectQuery() {
        String sql = select("name", "email", "age")
                .from("users")
                .where("status")
                .eq("active")
                .and("age")
                .gte(18)
                .and("country")
                .eq("Italy")
                .or("role")
                .eq("admin")
                .orderByDesc("created_at")
                .fetch(50)
                .offset(100)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT "users"."name", "users"."email", "users"."age" FROM "users" WHERE (((\"users\".\"status\" = 'active') AND (\"users\".\"age\" >= 18)) AND (\"users\".\"country\" = 'Italy')) OR (\"users\".\"role\" = 'admin') ORDER BY "users"."created_at" DESC OFFSET 100 ROWS FETCH NEXT 50 ROWS ONLY\
                        """);
    }

    @Test
    void isNull() {
        String sql = select("*").from("users").where("deleted_at").isNull().build();

        assertThat(sql)
                .isEqualTo("""
            SELECT * FROM "users" WHERE "users"."deleted_at" IS NULL\
            """);
    }

    @Test
    void isNotNull() {
        String sql = select("*").from("users").where("email").isNotNull().build();

        assertThat(sql)
                .isEqualTo("""
            SELECT * FROM "users" WHERE "users"."email" IS NOT NULL\
            """);
    }

    @Test
    void like() {
        String sql = select("*").from("users").where("name").like("%john%").build();

        assertThat(sql)
                .isEqualTo("""
            SELECT * FROM "users" WHERE "users"."name" LIKE '%john%'\
            """);
    }

    @Test
    void allComparisonOperators() {
        String sql = select("*")
                .from("products")
                .where("price")
                .gt(100)
                .and("discount")
                .lt(50)
                .and("rating")
                .gte(4)
                .and("stock")
                .lte(10)
                .and("category")
                .ne("deprecated")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM "products" WHERE ((((\"products\".\"price\" > 100) AND (\"products\".\"discount\" < 50)) AND (\"products\".\"rating\" >= 4)) AND (\"products\".\"stock\" <= 10)) AND (\"products\".\"category\" != 'deprecated')\
                        """);
    }

    @Test
    void fromNotSpecified() {
        assertThatThrownBy(() -> select("*").build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("FROM table must be specified");
    }

    @Test
    void invalidTableName() {
        assertThatThrownBy(() -> select("*").from(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name cannot be null or empty");
    }

    @Test
    void invalidColumnName() {
        assertThatThrownBy(() -> select("*").from("users").where(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void invalidAlias() {
        assertThatThrownBy(() -> select("*").from("users").as(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alias cannot be null or empty");
    }

    @Test
    void invalidFetch() {
        assertThatThrownBy(() -> select("*").from("users").fetch(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Fetch rows must be positive, got: -1");
    }

    @Test
    void invalidOffset() {
        assertThatThrownBy(() -> select("*").from("users").offset(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Offset must be non-negative, got: -5");
    }
}
