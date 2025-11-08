package lan.tlab.r4j.sql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.OverClause;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.WindowFunction;
import lan.tlab.r4j.sql.ast.dql.clause.Sorting;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderWindowFunctionsTest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void select_withRowNumber_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.rowNumber(OverClause.builder()
                                .orderBy(Sorting.desc(ColumnReference.of("employees", "salary")))
                                .build()),
                        "row_num")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", ROW_NUMBER() OVER (ORDER BY "employees"."salary" DESC) AS row_num FROM "employees"\
            """);
    }

    @Test
    void select_withRowNumberPartitionBy_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "department")
                .column("employees", "salary")
                .expression(
                        WindowFunction.rowNumber(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .orderBy(Sorting.desc(ColumnReference.of("employees", "salary")))
                                .build()),
                        "dept_row_num")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."department", "employees"."salary", ROW_NUMBER() OVER (PARTITION BY "employees"."department" ORDER BY "employees"."salary" DESC) AS dept_row_num FROM "employees"\
            """);
    }

    @Test
    void select_withRank_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.rank(OverClause.builder()
                                .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                .build()),
                        "salary_rank")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", RANK() OVER (ORDER BY "employees"."salary" DESC) AS salary_rank FROM "employees"\
            """);
    }

    @Test
    void select_withDenseRank_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.denseRank(OverClause.builder()
                                .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                .build()),
                        "salary_dense_rank")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", DENSE_RANK() OVER (ORDER BY "employees"."salary" DESC) AS salary_dense_rank FROM "employees"\
            """);
    }

    @Test
    void select_withNtile_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.ntile(
                                4,
                                OverClause.builder()
                                        .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                        .build()),
                        "quartile")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", NTILE(4) OVER (ORDER BY "employees"."salary" DESC) AS quartile FROM "employees"\
            """);
    }

    @Test
    void select_withLag_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.lag(
                                ColumnReference.of("employees", "salary"),
                                1,
                                OverClause.builder()
                                        .orderBy(Sorting.asc(ColumnReference.of("employees", "hire_date")))
                                        .build()),
                        "prev_salary")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", LAG("employees"."salary", 1) OVER (ORDER BY "employees"."hire_date" ASC) AS prev_salary FROM "employees"\
            """);
    }

    @Test
    void select_withLagWithDefaultValue_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.lag(
                                ColumnReference.of("employees", "salary"),
                                1,
                                Literal.of(0),
                                OverClause.builder()
                                        .orderBy(Sorting.desc(ColumnReference.of("employees", "hire_date")))
                                        .build()),
                        "prev_salary")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", LAG("employees"."salary", 1, 0) OVER (ORDER BY "employees"."hire_date" DESC) AS prev_salary FROM "employees"\
            """);
    }

    @Test
    void select_withLead_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.lead(
                                ColumnReference.of("employees", "salary"),
                                1,
                                OverClause.builder()
                                        .orderBy(Sorting.desc(ColumnReference.of("employees", "hire_date")))
                                        .build()),
                        "next_salary")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", LEAD("employees"."salary", 1) OVER (ORDER BY "employees"."hire_date" DESC) AS next_salary FROM "employees"\
            """);
    }

    @Test
    void select_withLeadWithDefaultValue_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.lead(
                                ColumnReference.of("employees", "salary"),
                                1,
                                Literal.of(0),
                                OverClause.builder()
                                        .orderBy(Sorting.desc(ColumnReference.of("employees", "hire_date")))
                                        .build()),
                        "next_salary")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", LEAD("employees"."salary", 1, 0) OVER (ORDER BY "employees"."hire_date" DESC) AS next_salary FROM "employees"\
            """);
    }

    @Test
    void select_withMultipleWindowFunctions_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.rowNumber(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                .build()),
                        "row_num")
                .expression(
                        WindowFunction.rank(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                .build()),
                        "dept_rank")
                .expression(
                        WindowFunction.ntile(
                                4,
                                OverClause.builder()
                                        .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                        .build()),
                        "quartile")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", ROW_NUMBER() OVER (PARTITION BY "employees"."department" ORDER BY "employees"."salary" DESC) AS row_num, RANK() OVER (PARTITION BY "employees"."department" ORDER BY "employees"."salary" DESC) AS dept_rank, NTILE(4) OVER (ORDER BY "employees"."salary" DESC) AS quartile FROM "employees"\
            """);
    }

    @Test
    void select_withRowNumberPartitionByOrderByAsc_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "department")
                .column("employees", "salary")
                .expression(
                        WindowFunction.rowNumber(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .orderBy(Sorting.asc(ColumnReference.of("employees", "hire_date")))
                                .build()),
                        "hire_order")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."department", "employees"."salary", ROW_NUMBER() OVER (PARTITION BY "employees"."department" ORDER BY "employees"."hire_date" ASC) AS hire_order FROM "employees"\
            """);
    }

    @Test
    void select_withRankPartitionByOrderByDesc_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.rank(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .orderBy(Sorting.desc(ColumnReference.of("employees", "salary")))
                                .build()),
                        "dept_salary_rank")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", RANK() OVER (PARTITION BY "employees"."department" ORDER BY "employees"."salary" DESC) AS dept_salary_rank FROM "employees"\
            """);
    }

    @Test
    void select_withDenseRankOrderByAsc_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.denseRank(OverClause.builder()
                                .orderBy(Sorting.asc(ColumnReference.of("employees", "hire_date")))
                                .build()),
                        "hire_dense_rank")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", DENSE_RANK() OVER (ORDER BY "employees"."hire_date" ASC) AS hire_dense_rank FROM "employees"\
            """);
    }

    @Test
    void select_withNtileWithDifferentBuckets_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.ntile(
                                10,
                                OverClause.builder()
                                        .orderBy(Sorting.desc(ColumnReference.of("employees", "salary")))
                                        .build()),
                        "decile")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", NTILE(10) OVER (ORDER BY "employees"."salary" DESC) AS decile FROM "employees"\
            """);
    }

    @Test
    void select_withLagWithOffset2_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.lag(
                                ColumnReference.of("employees", "salary"),
                                2,
                                OverClause.builder()
                                        .partitionBy(ColumnReference.of("employees", "department"))
                                        .orderBy(Sorting.asc(ColumnReference.of("employees", "hire_date")))
                                        .build()),
                        "salary_two_back")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", LAG("employees"."salary", 2) OVER (PARTITION BY "employees"."department" ORDER BY "employees"."hire_date" ASC) AS salary_two_back FROM "employees"\
            """);
    }

    @Test
    void select_withLeadWithOffset3AndDefault_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "salary")
                .expression(
                        WindowFunction.lead(
                                ColumnReference.of("employees", "salary"),
                                3,
                                Literal.of(-1),
                                OverClause.builder()
                                        .partitionBy(ColumnReference.of("employees", "department"))
                                        .orderBy(Sorting.asc(ColumnReference.of("employees", "hire_date")))
                                        .build()),
                        "salary_three_ahead")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."salary", LEAD("employees"."salary", 3, -1) OVER (PARTITION BY "employees"."department" ORDER BY "employees"."hire_date" ASC) AS salary_three_ahead FROM "employees"\
            """);
    }

    @Test
    void select_withRowNumberOnlyOrderBy_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .expression(
                        WindowFunction.rowNumber(OverClause.builder()
                                .orderBy(Sorting.asc(ColumnReference.of("employees", "employee_id")))
                                .build()),
                        "id_order")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", ROW_NUMBER() OVER (ORDER BY "employees"."employee_id" ASC) AS id_order FROM "employees"\
            """);
    }

    @Test
    void select_withRankOnlyPartitionBy_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "department")
                .column("employees", "salary")
                .expression(
                        WindowFunction.rank(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .build()),
                        "dept_rank_no_order")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."department", "employees"."salary", RANK() OVER (PARTITION BY "employees"."department") AS dept_rank_no_order FROM "employees"\
            """);
    }

    @Test
    void select_withDenseRankPartitionByMultipleColumns_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "department")
                .column("employees", "job_title")
                .column("employees", "salary")
                .expression(
                        WindowFunction.denseRank(OverClause.builder()
                                .partitionBy(
                                        ColumnReference.of("employees", "department"),
                                        ColumnReference.of("employees", "job_title"))
                                .orderBy(List.of(Sorting.desc(ColumnReference.of("employees", "salary"))))
                                .build()),
                        "dept_job_rank")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."department", "employees"."job_title", "employees"."salary", DENSE_RANK() OVER (PARTITION BY "employees"."department", "employees"."job_title" ORDER BY "employees"."salary" DESC) AS dept_job_rank FROM "employees"\
            """);
    }

    @Test
    void select_withNtilePartitionByOrderByMultipleColumns_generatesCorrectSql() {
        String result = dsl.select()
                .column("employees", "employee_id")
                .column("employees", "name")
                .column("employees", "department")
                .column("employees", "salary")
                .expression(
                        WindowFunction.ntile(
                                5,
                                OverClause.builder()
                                        .partitionBy(ColumnReference.of("employees", "department"))
                                        .orderBy(List.of(
                                                Sorting.asc(ColumnReference.of("employees", "hire_date")),
                                                Sorting.desc(ColumnReference.of("employees", "salary"))))
                                        .build()),
                        "dept_hire_quintile")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
            SELECT "employees"."employee_id", "employees"."name", "employees"."department", "employees"."salary", NTILE(5) OVER (PARTITION BY "employees"."department" ORDER BY "employees"."hire_date" ASC, "employees"."salary" DESC) AS dept_hire_quintile FROM "employees"\
            """);
    }
}
