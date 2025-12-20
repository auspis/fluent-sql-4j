package lan.tlab.r4j.jdsql.dsl.select;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.predicate.AndOr;
import lan.tlab.r4j.jdsql.ast.core.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.core.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.core.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.clause.LogicalCombinator;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderTest {

    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
    }

    @Test
    void ok() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "name", "email")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT \"name\", \"email\" FROM \"users\"");
    }

    @Test
    void star() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("products")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"products\"");
    }

    @Test
    void fromWithAlias() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "name", "email")
                .from("users")
                .as("u")
                .where()
                .column("name")
                .eq("John")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                            SELECT "name", "email" FROM "users" AS u WHERE "name" = ?\
                            """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John");
    }

    @Test
    void where() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("age")
                .gt(18)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" WHERE \"age\" > ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
    }

    @Test
    void and() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "name", "email")
                .from("users")
                .where()
                .column("age")
                .gt(18)
                .and()
                .column("status")
                .eq("active")
                .and()
                .column("country")
                .eq("Italy")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT \"name\", \"email\" FROM \"users\" WHERE ((\"age\" > ?) AND (\"status\" = ?)) AND (\"country\" = ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "active");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "Italy");
    }

    @Test
    void or() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("role")
                .eq("admin")
                .or()
                .column("role")
                .eq("moderator")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" WHERE (\"role\" = ?) OR (\"role\" = ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "admin");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "moderator");
    }

    @Test
    void andOr() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("status")
                .eq("active")
                .and()
                .column("age")
                .gt(18)
                .or()
                .column("role")
                .eq("admin")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT * FROM \"users\" WHERE ((\"status\" = ?) AND (\"age\" > ?)) OR (\"role\" = ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "active");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "admin");
    }

    @Test
    void orderBy() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "name", "age")
                .from("users")
                .orderBy("name")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT \"name\", \"age\" FROM \"users\" ORDER BY \"name\" ASC");
    }

    @Test
    void orderByDesc() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .orderByDesc("created_at")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"products\" ORDER BY \"created_at\" DESC");
    }

    @Test
    void fetch() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .fetch(10)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" FETCH NEXT 10 ROWS ONLY");
    }

    @Test
    void fetchWithOffset() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .fetch(10)
                .offset(20)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY");
    }

    @Test
    void offsetBeforeFetch() throws SQLException {
        PreparedStatement sql = new SelectBuilder(specFactory, "*")
                .from("users")
                .offset(15)
                .fetch(5)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(sql).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" OFFSET 15 ROWS FETCH NEXT 5 ROWS ONLY\
                """);
    }

    @Test
    void offsetZero() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("users")
                .fetch(5)
                .offset(0)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" FETCH NEXT 5 ROWS ONLY");
    }

    @Test
    void offsetOverridesOnMultipleCalls() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("users")
                .offset(10)
                .offset(20)
                .fetch(5)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" OFFSET 20 ROWS FETCH NEXT 5 ROWS ONLY");
    }

    @Test
    void offsetPreservedAcrossFetchChanges() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("users")
                .fetch(10)
                .offset(25)
                .fetch(8)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" OFFSET 25 ROWS FETCH NEXT 8 ROWS ONLY");
    }

    @Test
    void offsetPrecisionMaintained() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("users")
                .offset(23)
                .fetch(10)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" OFFSET 23 ROWS FETCH NEXT 10 ROWS ONLY");
    }

    @Test
    void fullSelectQuery() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "name", "email", "age")
                .from("users")
                .where()
                .column("status")
                .eq("active")
                .and()
                .column("age")
                .gte(18)
                .and()
                .column("country")
                .eq("Italy")
                .or()
                .column("role")
                .eq("admin")
                .orderByDesc("created_at")
                .fetch(50)
                .offset(100)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .contains("SELECT \"name\", \"email\", \"age\" FROM \"users\" WHERE")
                .contains("ORDER BY \"created_at\" DESC")
                .contains("OFFSET 100 ROWS FETCH NEXT 50 ROWS ONLY");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "active");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "Italy");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "admin");
    }

    @Test
    void allComparisonOperators() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("price")
                .gt(100)
                .and()
                .column("discount")
                .lt(50)
                .and()
                .column("rating")
                .gte(4)
                .and()
                .column("stock")
                .lte(10)
                .and()
                .column("category")
                .ne("deprecated")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .contains("SELECT * FROM \"products\" WHERE")
                .contains("> ?")
                .contains("< ?")
                .contains(">= ?")
                .contains("<= ?")
                .contains("<> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 50);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 4);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 10);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(5, "deprecated");
    }

    @Test
    void fromNotSpecified() {
        assertThatThrownBy(() ->
                        new SelectBuilder(specFactory, "*").buildPreparedStatement(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("FROM table must be specified");
    }

    @Test
    void invalidTableName() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*").from(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TableIdentifier name cannot be null or empty");
    }

    @Test
    void invalidColumnName() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*")
                        .from("users")
                        .where()
                        .column(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void invalidAlias() {
        assertThatThrownBy(
                        () -> new SelectBuilder(specFactory, "*").from("users").as(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alias cannot be null or empty");
    }

    @Test
    void invalidFetch() {
        assertThatThrownBy(
                        () -> new SelectBuilder(specFactory, "*").from("users").fetch(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Fetch rows must be positive, got: -1");
    }

    @Test
    void invalidOffset() {
        assertThatThrownBy(
                        () -> new SelectBuilder(specFactory, "*").from("users").offset(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Offset must be non-negative, got: -5");
    }

    // Tests for static helper methods
    @Test
    void hasValidConditionReturnsTrueForValidComparison() {
        Where whereWithComparison = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        assertThat(SelectBuilder.hasValidCondition(whereWithComparison)).isTrue();
    }

    @Test
    void hasValidConditionReturnsFalseForNullPredicate() {
        Where whereWithNull = Where.of(new NullPredicate());

        assertThat(SelectBuilder.hasValidCondition(whereWithNull)).isFalse();
    }

    @Test
    void combineWithExistingCreatesAndCondition() {
        Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

        Where result = SelectBuilder.combineWithExisting(existingWhere, newCondition, LogicalCombinator.AND);

        assertThat(result.condition()).isInstanceOf(AndOr.class);
    }

    @Test
    void combineWithExistingCreatesOrCondition() {
        Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

        Where result = SelectBuilder.combineWithExisting(existingWhere, newCondition, LogicalCombinator.OR);

        assertThat(result.condition()).isInstanceOf(AndOr.class);
        AndOr andOr = (AndOr) result.condition();

        assertThat(andOr.operator()).isEqualTo(lan.tlab.r4j.jdsql.ast.core.predicate.LogicalOperator.OR);
    }

    @Test
    void combineConditionsWithNullWhereCreatesNewCondition() {
        Predicate condition = Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John"));

        Where result = SelectBuilder.combineConditions(null, condition, LogicalCombinator.AND);

        assertThat(result.condition()).isEqualTo(condition);
    }

    @Test
    void combineConditionsWithValidWhereCreatesCombinedCondition() {
        Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

        Where result = SelectBuilder.combineConditions(existingWhere, newCondition, LogicalCombinator.OR);

        assertThat(result.condition()).isInstanceOf(AndOr.class);
    }

    @Test
    void combineConditionsWithNullPredicateCreatesNewCondition() {
        Where existingWhere = Where.of(new NullPredicate());

        Predicate newCondition = Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John"));

        Where result = SelectBuilder.combineConditions(existingWhere, newCondition, LogicalCombinator.AND);

        assertThat(result.condition()).isEqualTo(newCondition);
    }

    @Test
    void fromSubquery() throws SQLException {
        SelectBuilder subquery = new SelectBuilder(specFactory, "id", "name")
                .from("users")
                .where()
                .column("age")
                .gt(18);

        new SelectBuilder(specFactory, "*")
                .from(subquery, "u")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        SELECT * FROM \
                        (SELECT "users"."id", "users"."name" FROM "users" WHERE "users"."age" > ?) \
                        AS "u"\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
    }

    @Test
    void fromSubqueryWithWhere() throws SQLException {
        SelectBuilder subquery = new SelectBuilder(specFactory, "id", "total")
                .from("orders")
                .where()
                .column("status")
                .eq("completed");

        PreparedStatement sql = new SelectBuilder(specFactory, "*")
                .from(subquery, "o")
                .where()
                .column("total")
                .gt(100)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(sql).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        SELECT * \
                        FROM (SELECT "orders"."id", "orders"."total" FROM "orders" WHERE "orders"."status" = ?) AS "o" \
                        WHERE "o"."total" > ?\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "completed");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 100);
    }

    @Test
    void whereWithScalarSubquery() throws SQLException {
        SelectBuilder subquery = new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("age")
                .gt(50);

        PreparedStatement result = new SelectBuilder(specFactory, "name")
                .from("employees")
                .where()
                .column("age")
                .gt(subquery)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "employees"."name" FROM "employees" \
                WHERE "employees"."age" > (SELECT * FROM "users" WHERE "users"."age" > ?)\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 50);
    }

    @Test
    void whereWithScalarSubqueryEquals() throws SQLException {
        SelectBuilder maxAgeSubquery = new SelectBuilder(specFactory, "*").from("users");

        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("employees")
                .where()
                .column("age")
                .eq(maxAgeSubquery)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "employees" \
                WHERE "employees"."age" = (SELECT * FROM "users")\
                """);
    }

    @Test
    void fromSubqueryNullSubquery() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*").from((SelectBuilder) null, "alias"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Subquery cannot be null");
    }

    @Test
    void fromSubqueryNullAlias() {
        SelectBuilder subquery = new SelectBuilder(specFactory, "*").from("users");
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*").from(subquery, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alias cannot be null or empty for subquery");
    }

    @Test
    void fromSubqueryEmptyAlias() {
        SelectBuilder subquery = new SelectBuilder(specFactory, "*").from("users");
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*").from(subquery, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alias cannot be null or empty for subquery");
    }

    @Test
    void havingWithSingleCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .groupBy("age")
                .having()
                .column("age")
                .gt(18)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        SELECT * FROM "users" GROUP BY "age" HAVING "age" > ?\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
    }

    @Test
    void havingWithAndCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .groupBy("age")
                .having()
                .column("age")
                .gt(18)
                .andHaving()
                .column("age")
                .lt(65)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        SELECT * FROM "users" \
                        GROUP BY "age" \
                        HAVING ("age" > ?) AND ("age" < ?)\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 65);
    }

    @Test
    void havingWithOrCondition() throws SQLException {
        PreparedStatement sql = new SelectBuilder(specFactory, "*")
                .from("users")
                .groupBy("status")
                .having()
                .column("status")
                .eq("active")
                .orHaving()
                .column("status")
                .eq("pending")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(sql).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        SELECT * \
                        FROM "users" \
                        GROUP BY "status" \
                        HAVING ("status" = ?) OR ("status" = ?)\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "active");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "pending");
    }

    @Test
    void havingWithComplexConditions() throws SQLException {
        PreparedStatement sql = new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy("customer_id")
                .having()
                .column("customer_id")
                .gt(100)
                .andHaving()
                .column("customer_id")
                .lt(500)
                .orHaving()
                .column("customer_id")
                .eq(999)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(sql).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        SELECT * \
                        FROM "orders" \
                        GROUP BY "customer_id" \
                        HAVING (("customer_id" > ?) \
                        AND ("customer_id" < ?)) \
                        OR ("customer_id" = ?)\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 500);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 999);
    }

    @Test
    void havingWithWhereAndOrderBy() throws SQLException {
        PreparedStatement sql = new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("category")
                .eq("electronics")
                .groupBy("brand")
                .having()
                .column("brand")
                .like("%Apple%")
                .orderBy("brand")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(sql).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        SELECT * \
                        FROM "products" \
                        WHERE "category" = ? \
                        GROUP BY "brand" HAVING "brand" LIKE ? \
                        ORDER BY "brand" ASC\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "electronics");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "%Apple%");
    }

    @Test
    void invalidHavingColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*")
                        .from("users")
                        .groupBy("age")
                        .having()
                        .column(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    // Additional HAVING tests for better HavingConditionBuilder coverage

    @Test
    void havingStringComparisons() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .groupBy("category")
                .having()
                .column("category")
                .eq("electronics")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"category\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "electronics");

        new SelectBuilder(specFactory, "*")
                .from("products")
                .groupBy("category")
                .having()
                .column("category")
                .ne("books")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"category\" <> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "books");
    }

    @Test
    void havingNumberComparisons() throws SQLException {
        // Test additional number comparison operators
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy("total")
                .having()
                .column("total")
                .gte(100.0)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"total\" >= ?");

        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy("total")
                .having()
                .column("total")
                .lte(500)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"total\" <= ?");
    }

    @Test
    void havingBooleanComparisons() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .groupBy("active")
                .having()
                .column("active")
                .eq(true)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"active\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, true);

        new SelectBuilder(specFactory, "*")
                .from("users")
                .groupBy("active")
                .having()
                .column("active")
                .ne(false)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"active\" <> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, false);
    }

    @Test
    void havingNullChecks() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .groupBy("email")
                .having()
                .column("email")
                .isNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"email\" IS NULL");

        new SelectBuilder(specFactory, "*")
                .from("users")
                .groupBy("email")
                .having()
                .column("email")
                .isNotNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"email\" IS NOT NULL");
    }

    @Test
    void havingBetweenNumbers() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("products")
                .groupBy("price")
                .having()
                .column("price")
                .between(10.0, 100.0)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).contains("HAVING \"price\" BETWEEN ? AND ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 10.0);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 100.0);
    }

    @Test
    void havingBetweenDates() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy("order_date")
                .having()
                .column("order_date")
                .between(java.time.LocalDate.of(2023, 1, 1), java.time.LocalDate.of(2023, 12, 31))
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).contains("HAVING \"order_date\" BETWEEN ? AND ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, java.time.LocalDate.of(2023, 1, 1));
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, java.time.LocalDate.of(2023, 12, 31));
    }

    @Test
    void havingSubqueryComparison() throws SQLException {
        // Create a simple subquery without aggregate functions
        SelectBuilder subquery = new SelectBuilder(specFactory, "budget")
                .from("departments")
                .where()
                .column("active")
                .eq(true);

        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("departments")
                .groupBy("budget")
                .having()
                .column("budget")
                .gt(subquery)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .contains(
                        "HAVING \"departments\".\"budget\" > (SELECT \"departments\".\"budget\" FROM \"departments\" WHERE \"departments\".\"active\" = ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, true);
    }

    @Test
    void havingWithNullSubquery_throwsException() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*")
                        .from("users")
                        .groupBy("age")
                        .having()
                        .column("age")
                        .eq((SelectBuilder) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Subquery cannot be null");
    }

    @Test
    void havingInOperatorWithStrings() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .groupBy("category")
                .having()
                .column("category")
                .in("electronics", "books", "toys")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"category\" IN (?, ?, ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "electronics");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "books");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "toys");
    }

    @Test
    void havingInOperatorWithNumbers() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy("customer_id")
                .having()
                .column("customer_id")
                .in(100, 200, 300)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"customer_id\" IN (?, ?, ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 200);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 300);
    }

    @Test
    void havingInOperatorWithBooleans() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .groupBy("active")
                .having()
                .column("active")
                .in(true)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"active\" IN (?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, true);
    }

    @Test
    void havingInOperatorWithDates() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("events")
                .groupBy("event_date")
                .having()
                .column("event_date")
                .in(java.time.LocalDate.of(2023, 1, 1), java.time.LocalDate.of(2023, 12, 31))
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" IN (?, ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, java.time.LocalDate.of(2023, 1, 1));
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, java.time.LocalDate.of(2023, 12, 31));
    }

    @Test
    void havingInOperatorWithAndCondition() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("products")
                .groupBy("category", "brand")
                .having()
                .column("category")
                .in("electronics", "computers")
                .andHaving()
                .column("brand")
                .ne("unknown")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).contains("HAVING (\"category\" IN (?, ?)) AND (\"brand\" <> ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "electronics");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "computers");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "unknown");
    }

    @Test
    void havingInOperatorWithOrCondition() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy("status")
                .having()
                .column("status")
                .in("completed", "shipped")
                .orHaving()
                .column("status")
                .eq("delivered")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).contains("HAVING (\"status\" IN (?, ?)) OR (\"status\" = ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "completed");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "shipped");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "delivered");
    }

    @Test
    void havingInOperatorEmptyValues_throwsException() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*")
                        .from("users")
                        .groupBy("age")
                        .having()
                        .column("age")
                        .in(new String[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one value must be provided for IN clause");
    }

    @Test
    void havingInOperatorNullValues_throwsException() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*")
                        .from("users")
                        .groupBy("age")
                        .having()
                        .column("age")
                        .in((String[]) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one value must be provided for IN clause");
    }

    @Test
    void countStar() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .countStar()
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT COUNT(*) FROM "users"\
                """);
    }

    @Test
    void countStarWithAlias() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .countStar()
                .as("total")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT COUNT(*) AS "total" FROM "users"\
                """);
    }

    @Test
    void sum() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .sum("amount")
                .from("orders")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT SUM("amount") FROM "orders"\
                """);
    }

    @Test
    void sumWithAlias() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .sum("amount")
                .as("total_amount")
                .from("orders")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT SUM("amount") AS "total_amount" FROM "orders"\
                """);
    }

    @Test
    void avg() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .avg("score")
                .from("students")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT AVG("score") FROM "students"\
                """);
    }

    @Test
    void avgWithAlias() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .avg("score")
                .as("avg_score")
                .from("students")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT AVG("score") AS "avg_score" FROM "students"\
                """);
    }

    @Test
    void count() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .count("id")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT COUNT("id") FROM "users"\
                """);
    }

    @Test
    void countWithAlias() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .count("id")
                .as("user_count")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT COUNT("id") AS "user_count" FROM "users"\
                """);
    }

    @Test
    void countDistinct() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .countDistinct("email")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT COUNT(DISTINCT "email") FROM "users"\
                """);
    }

    @Test
    void countDistinctWithAlias() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .countDistinct("email")
                .as("unique_emails")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT COUNT(DISTINCT "email") AS "unique_emails" FROM "users"\
                """);
    }

    @Test
    void max() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .max("price")
                .from("products")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT MAX("price") FROM "products"\
                """);
    }

    @Test
    void maxWithAlias() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .max("price")
                .as("max_price")
                .from("products")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT MAX("price") AS "max_price" FROM "products"\
                """);
    }

    @Test
    void min() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .min("price")
                .from("products")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT MIN("price") FROM "products"\
                """);
    }

    @Test
    void minWithAlias() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .min("price")
                .as("min_price")
                .from("products")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT MIN("price") AS "min_price" FROM "products"\
                """);
    }

    @Test
    void sumWithGroupBy() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .sum("amount")
                .from("orders")
                .groupBy("customer_id")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT SUM("amount") FROM "orders" GROUP BY "customer_id"\
                """);
    }

    @Test
    void countWithWhere() throws SQLException {
        new SelectProjectionBuilder<>(specFactory)
                .countStar()
                .from("users")
                .where()
                .column("active")
                .eq(true)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT COUNT(*) FROM "users" WHERE "active" = ?\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, true);
    }

    @Test
    void avgWithGroupByAndHaving() throws SQLException {
        new SelectProjectionBuilder<>(specFactory)
                .avg("salary")
                .from("employees")
                .groupBy("department")
                .having()
                .column("department")
                .ne("HR")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        SELECT AVG("salary") \
                        FROM "employees" \
                        GROUP BY "department" \
                        HAVING "department" <> ?\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "HR");
    }

    @Test
    void multipleAggregatesWithoutAliases() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .sum("score")
                .max("createdAt")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        SELECT SUM("score"), MAX("createdAt") FROM "users"\
                        """);
    }

    @Test
    void multipleAggregatesWithAliases() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .sum("score")
                .as("total_score")
                .max("createdAt")
                .as("latest")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        SELECT SUM("score") AS "total_score", MAX("createdAt") AS "latest" \
                        FROM "users"\
                        """);
    }

    @Test
    void multipleAggregatesWithOneAlias() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .sum("score")
                .max("createdAt")
                .as("latest")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        SELECT SUM("score"), MAX("createdAt") AS "latest" \
                        FROM "users"\
                        """);
    }

    @Test
    void column() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .column("name")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name" FROM "users"\
                """);
    }

    @Test
    void columnWithAlias() throws SQLException {
        new SelectProjectionBuilder<>(specFactory)
                .column("name")
                .as("user_name")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name" AS "user_name" FROM "users"\
                """);
    }

    @Test
    void multipleColumns() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .column("name")
                .column("email")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", "email" FROM "users"\
                """);
    }

    @Test
    void multipleColumnsWithAliases() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .column("name")
                .as("user_name")
                .column("email")
                .as("user_email")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name" AS "user_name", "email" AS "user_email" FROM "users"\
                """);
    }

    @Test
    void multipleColumnsWithOneAlias() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .column("name")
                .column("email")
                .as("user_email")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", "email" AS "user_email" FROM "users"\
                """);
    }

    @Test
    void tableQualifiedColumn() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .column("users", "name")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name" FROM "users"\
                """);
    }

    @Test
    void mixedColumnsAndAggregates() throws SQLException {
        PreparedStatement result = new SelectProjectionBuilder<>(specFactory)
                .column("name")
                .sum("score")
                .as("total_score")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", SUM("score") AS "total_score" FROM "users"\
                """);
    }
}
