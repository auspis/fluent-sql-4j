package lan.tlab.r4j.jdsql.ast.visitor.sql;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.dql.clause.From;
import lan.tlab.r4j.jdsql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.jdsql.ast.dql.clause.Having;
import lan.tlab.r4j.jdsql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.clause.Sorting;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.dql.projection.AggregateCallProjection;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Standard2008SqlRendererTest {

    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void selectFrom() {
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("Customer", "name"))))
                .from(From.fromTable("Customer", "c"))
                .build();

        String sql = statement.accept(renderer, new AstContext());
        assertThat(sql).isEqualTo("SELECT \"Customer\".\"name\" FROM \"Customer\" AS c");
    }

    @Test
    void selectFromWhereGroupByHavingOrder() {
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("Customer", "name")),
                        new AggregateCallProjection(AggregateCall.countStar(), "nameCounter")))
                .from(From.fromTable("Customer", "c"))
                .where(Where.of(Comparison.gt(ColumnReference.of("c", "score"), Literal.of(400))))
                .groupBy(GroupBy.of(ColumnReference.of("Customer", "name")))
                .having(Having.of(Comparison.gt(
                        AggregateCall.count(ColumnReference.of("Customer", "customer_id")), Literal.of(10))))
                .orderBy(OrderBy.of(Sorting.desc(ColumnReference.of("Customer", "name"))))
                .build();

        String sql = statement.accept(renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
         	SELECT \"Customer\".\"name\", COUNT(*) AS nameCounter \
         	FROM \"Customer\" AS c \
         	WHERE \"c\".\"score\" > 400 \
         	GROUP BY \"Customer\".\"name\" \
         	HAVING COUNT(\"Customer\".\"customer_id\") > 10 \
         	ORDER BY \"Customer\".\"name\" DESC\
         	""");
    }
    //
    //    @Test
    //    void testSelectWithFetchFirst() {
    //        // SELECT "id" FROM "items" FETCH FIRST 10 ROWS ONLY
    //        SelectStatement statement = SelectStatement.builder()
    //                .select(ColumnReference.of("Customer", "id"))
    //                .from(new TableReference(null, "items"))
    //                .limit(10)
    //                .build();
    //
    //        String sql = statement.accept(renderer);
    //        assertThat(sql).isEqualTo("SELECT \"id\" FROM \"items\" FETCH FIRST 10 ROWS ONLY");
    //    }
    //
    //    @Test
    //    void testSelectWithOffsetAndFetchNext() {
    //        // SELECT "id" FROM "items" OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY
    //        SelectStatement statement = SelectStatement.builder()
    //                .select(ColumnReference.of("Customer", "id"))
    //                .from(new TableReference(null, "items"))
    //                .limit(20, 10) // offset, limit
    //                .build();
    //
    //        String sql = statement.accept(renderer);
    //        assertThat(sql).isEqualTo("SELECT \"id\" FROM \"items\" OFFSET 20 ROWS FETCH NEXT 10
    // ROWS ONLY");
    //    }
    //
    //    @Test
    //    void testColumnNameIsQuoted() {
    //        // Assicuriamoci che la logica di quotazione sia applicata correttamente
    //        ColumnReference<?> column = ColumnReference.of("Customer", "my_column");
    //        String sql = column.accept(renderer);
    //        assertThat(sql).isEqualTo("\"my_column\"");
    //    }

}
