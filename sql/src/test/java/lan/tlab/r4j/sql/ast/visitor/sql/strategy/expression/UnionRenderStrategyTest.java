package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.set.UnionExpression;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnionRenderStrategyTest {

    private UnionRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new UnionRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void union() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer", "email")))
                        .build())
                .from(From.fromTable("Customer"))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Account", "email")))
                        .build())
                .from(From.fromTable("Account"))
                .build();

        UnionExpression union = UnionExpression.union(select1, select2);
        String sql = strategy.render(union, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
			((SELECT \"Customer\".\"email\" FROM \"Customer\") \
			UNION \
			(SELECT \"Account\".\"email\" FROM \"Account\"))\
			""");
    }

    @Test
    void union_multiColumns() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer", "email")))
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer", "surname")))
                        .build())
                .from(From.fromTable("Customer"))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Account", "email")))
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Account", "surname")))
                        .build())
                .from(From.fromTable("Account"))
                .build();

        UnionExpression union = UnionExpression.union(select1, select2);
        String sql = strategy.render(union, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
			((SELECT \"Customer\".\"email\", \"Customer\".\"surname\" FROM \"Customer\") \
			UNION \
			(SELECT \"Account\".\"email\", \"Account\".\"surname\" FROM \"Account\"))\
			""");
    }

    @Test
    void unionAll() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer", "email")))
                        .build())
                .from(From.fromTable("Customer"))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Account", "email")))
                        .build())
                .from(From.fromTable("Account"))
                .build();

        UnionExpression union = UnionExpression.unionAll(select1, select2);
        String sql = strategy.render(union, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
			((SELECT \"Customer\".\"email\" FROM \"Customer\") \
			UNION ALL \
			(SELECT \"Account\".\"email\" FROM \"Account\"))\
			""");
    }

    @Test
    void unionAll_chained() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer", "email")))
                        .build())
                .from(From.fromTable("Customer"))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Account", "email")))
                        .build())
                .from(From.fromTable("Account"))
                .build();
        SelectStatement select3 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("LegacyAccount", "val"), "email"))
                        .build())
                .from(From.fromTable("LegacyAccount"))
                .build();

        UnionExpression firstUnion = UnionExpression.unionAll(select1, select2);
        UnionExpression union = UnionExpression.unionAll(firstUnion, select3);
        String sql = strategy.render(union, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
			((((SELECT \"Customer\".\"email\" FROM \"Customer\") \
			UNION ALL \
			(SELECT \"Account\".\"email\" FROM \"Account\"))) \
			UNION ALL \
			(SELECT \"LegacyAccount\".\"val\" AS email FROM \"LegacyAccount\"))\
			""");
    }
}
