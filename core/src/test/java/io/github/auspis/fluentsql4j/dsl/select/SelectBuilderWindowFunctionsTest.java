package io.github.auspis.fluentsql4j.dsl.select;

import static io.github.massimiliano.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.mockito.Mockito.verify;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.window.OverClause;
import io.github.auspis.fluentsql4j.ast.core.expression.window.WindowFunction;
import io.github.auspis.fluentsql4j.ast.dql.clause.Select;
import io.github.auspis.fluentsql4j.ast.dql.clause.Sorting;
import io.github.auspis.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.select.SelectBuilder;
import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.massimiliano.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderWindowFunctionsTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void select_withRowNumber_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.rowNumber(OverClause.builder()
                                .orderBy(Sorting.desc(ColumnReference.of("employees", "salary")))
                                .build()),
                        "row_num"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", \
            ROW_NUMBER() OVER (ORDER BY "salary" DESC) AS "row_num" \
            FROM "employees"\
            """);
    }

    @Test
    void select_withRowNumberPartitionBy_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "department")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.rowNumber(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .orderBy(Sorting.desc(ColumnReference.of("employees", "salary")))
                                .build()),
                        "dept_row_num"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "department", "salary", \
            ROW_NUMBER() OVER (PARTITION BY "department" ORDER BY "salary" DESC) AS "dept_row_num" \
            FROM "employees"\
            """);
    }

    @Test
    void select_withRank_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.rank(OverClause.builder()
                                .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                .build()),
                        "salary_rank"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", RANK() OVER (ORDER BY "salary" DESC) AS "salary_rank" FROM "employees"\
            """);
    }

    @Test
    void select_withDenseRank_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.denseRank(OverClause.builder()
                                .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                .build()),
                        "salary_dense_rank"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", DENSE_RANK() OVER (ORDER BY "salary" DESC) AS "salary_dense_rank" FROM "employees"\
            """);
    }

    @Test
    void select_withNtile_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.ntile(
                                4,
                                OverClause.builder()
                                        .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                        .build()),
                        "quartile"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", NTILE(4) OVER (ORDER BY "salary" DESC) AS "quartile" FROM "employees"\
            """);
    }

    @Test
    void select_withLag_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.lag(
                                ColumnReference.of("employees", "salary"),
                                1,
                                OverClause.builder()
                                        .orderBy(Sorting.asc(ColumnReference.of("employees", "hire_date")))
                                        .build()),
                        "prev_salary"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", LAG("salary", 1) OVER (ORDER BY "hire_date" ASC) AS "prev_salary" FROM "employees"\
            """);
    }

    @Test
    void select_withLagWithDefaultValue_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.lag(
                                ColumnReference.of("employees", "salary"),
                                1,
                                Literal.of(0),
                                OverClause.builder()
                                        .orderBy(Sorting.desc(ColumnReference.of("employees", "hire_date")))
                                        .build()),
                        "prev_salary"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", LAG("salary", 1, ?) OVER (ORDER BY "hire_date" DESC) AS "prev_salary" FROM "employees"\
            """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 0);
    }

    @Test
    void select_withLead_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.lead(
                                ColumnReference.of("employees", "salary"),
                                1,
                                OverClause.builder()
                                        .orderBy(Sorting.desc(ColumnReference.of("employees", "hire_date")))
                                        .build()),
                        "next_salary"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", LEAD("salary", 1) OVER (ORDER BY "hire_date" DESC) AS "next_salary" FROM "employees"\
            """);
    }

    @Test
    void select_withLeadWithDefaultValue_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.lead(
                                ColumnReference.of("employees", "salary"),
                                1,
                                Literal.of(0),
                                OverClause.builder()
                                        .orderBy(Sorting.desc(ColumnReference.of("employees", "hire_date")))
                                        .build()),
                        "next_salary"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", LEAD("salary", 1, ?) OVER (ORDER BY "hire_date" DESC) AS "next_salary" FROM "employees"\
            """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 0);
    }

    @Test
    void select_withMultipleWindowFunctions_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.rowNumber(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                .build()),
                        "row_num"))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.rank(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                .build()),
                        "dept_rank"))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.ntile(
                                4,
                                OverClause.builder()
                                        .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                        .build()),
                        "quartile"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", \
            ROW_NUMBER() OVER (PARTITION BY "department" ORDER BY "salary" DESC) AS "row_num", \
            RANK() OVER (PARTITION BY "department" ORDER BY "salary" DESC) AS "dept_rank", \
            NTILE(4) OVER (ORDER BY "salary" DESC) AS "quartile" \
            FROM "employees"\
            """);
    }

    @Test
    void select_withRowNumberPartitionByOrderByAsc_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "department")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.rowNumber(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .orderBy(Sorting.asc(ColumnReference.of("employees", "hire_date")))
                                .build()),
                        "hire_order"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "department", "salary", \
            ROW_NUMBER() OVER (PARTITION BY "department" ORDER BY "hire_date" ASC) AS "hire_order" \
            FROM "employees"\
            """);
    }

    @Test
    void select_withRankPartitionByOrderByDesc_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.rank(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .orderBy(Sorting.desc(ColumnReference.of("employees", "salary")))
                                .build()),
                        "dept_salary_rank"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", \
            RANK() OVER (PARTITION BY "department" ORDER BY "salary" DESC) AS "dept_salary_rank" \
            FROM "employees"\
            """);
    }

    @Test
    void select_withDenseRankOrderByAsc_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.denseRank(OverClause.builder()
                                .orderBy(Sorting.asc(ColumnReference.of("employees", "hire_date")))
                                .build()),
                        "hire_dense_rank"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", DENSE_RANK() OVER (ORDER BY "hire_date" ASC) AS "hire_dense_rank" \
            FROM "employees"\
            """);
    }

    @Test
    void select_withNtileWithDifferentBuckets_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.ntile(
                                10,
                                OverClause.builder()
                                        .orderBy(Sorting.desc(ColumnReference.of("employees", "salary")))
                                        .build()),
                        "decile"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", NTILE(10) OVER (ORDER BY "salary" DESC) AS "decile" \
            FROM "employees"\
            """);
    }

    @Test
    void select_withLagWithOffset2_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.lag(
                                ColumnReference.of("employees", "salary"),
                                2,
                                OverClause.builder()
                                        .partitionBy(ColumnReference.of("employees", "department"))
                                        .orderBy(Sorting.asc(ColumnReference.of("employees", "hire_date")))
                                        .build()),
                        "salary_two_back"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", \
            LAG("salary", 2) OVER (PARTITION BY "department" ORDER BY "hire_date" ASC) AS "salary_two_back" \
            FROM "employees"\
            """);
    }

    @Test
    void select_withLeadWithOffset3AndDefault_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.lead(
                                ColumnReference.of("employees", "salary"),
                                3,
                                Literal.of(-1),
                                OverClause.builder()
                                        .partitionBy(ColumnReference.of("employees", "department"))
                                        .orderBy(Sorting.asc(ColumnReference.of("employees", "hire_date")))
                                        .build()),
                        "salary_three_ahead"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "salary", \
            LEAD("salary", 3, ?) OVER (PARTITION BY "department" ORDER BY "hire_date" ASC) AS "salary_three_ahead" \
            FROM "employees"\
            """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, -1);
    }

    @Test
    void select_withRowNumberOnlyOrderBy_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.rowNumber(OverClause.builder()
                                .orderBy(Sorting.asc(ColumnReference.of("employees", "employee_id")))
                                .build()),
                        "id_order"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", ROW_NUMBER() OVER (ORDER BY "employee_id" ASC) AS "id_order" FROM "employees"\
            """);
    }

    @Test
    void select_withRankOnlyPartitionBy_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "department")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.rank(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .build()),
                        "dept_rank_no_order"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "department", "salary", RANK() OVER (PARTITION BY "department") AS "dept_rank_no_order" \
            FROM "employees"\
            """);
    }

    @Test
    void select_withDenseRankPartitionByMultipleColumns_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "department")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "job_title")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.denseRank(OverClause.builder()
                                .partitionBy(
                                        ColumnReference.of("employees", "department"),
                                        ColumnReference.of("employees", "job_title"))
                                .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                .build()),
                        "dept_job_rank"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "department", "job_title", "salary", \
            DENSE_RANK() OVER (PARTITION BY "department", "job_title" ORDER BY "salary" DESC) AS "dept_job_rank" \
            FROM "employees"\
            """);
    }

    @Test
    void select_withNtilePartitionByOrderByMultipleColumns_generatesCorrectSql() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "employee_id")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "department")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.ntile(
                                5,
                                OverClause.builder()
                                        .partitionBy(ColumnReference.of("employees", "department"))
                                        .orderBy(List.of(
                                                Sorting.asc(ColumnReference.of("employees", "hire_date")),
                                                Sorting.desc(ColumnReference.of("employees", "salary"))))
                                        .build()),
                        "dept_hire_quintile"))
                .build();

        new SelectBuilder(specFactory, select).from("employees").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
            SELECT "employee_id", "name", "department", "salary", \
            NTILE(5) OVER (PARTITION BY "department" ORDER BY "hire_date" ASC, "salary" DESC) AS "dept_hire_quintile" \
            FROM "employees"\
            """);
    }
}
