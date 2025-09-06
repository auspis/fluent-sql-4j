package lan.tlab.sqlbuilder.ast.visitor.composer.renderer;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.conditional.having.Having;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.Sorting;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Standard2008SqlRendererTest {

    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void selectFrom() {
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("Customer", "name"))))
                .from(From.fromTable("Customer", "c"))
                .build();

        String sql = statement.accept(renderer);
        assertThat(sql).isEqualTo("SELECT \"Customer\".\"name\" FROM \"Customer\" AS c");
    }

    @Test
    void selectFromWhereGroupByHavingOrder() {
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("Customer", "name")),
                        new AggregationFunctionProjection(AggregateCall.countStar(), "nameCounter")))
                .from(From.fromTable("Customer", "c"))
                .where(Where.of(Comparison.gt(ColumnReference.of("c", "score"), Literal.of(400))))
                .groupBy(GroupBy.of(ColumnReference.of("Customer", "name")))
                .having(Having.of(Comparison.gt(
                        AggregateCall.count(ColumnReference.of("Customer", "customer_id")), Literal.of(10))))
                .orderBy(OrderBy.of(Sorting.desc(ColumnReference.of("Customer", "name"))))
                .build();

        String sql = statement.accept(renderer);
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
