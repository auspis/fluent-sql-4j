package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.set.NullSetExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.set.UnionExpression;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertSource;
import lan.tlab.r4j.jdsql.ast.dql.clause.From;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlInsertSourcePsStrategyTest {

    private StandardSqlInsertSourcePsStrategy strategy;
    private PreparedStatementRenderer visitor;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlInsertSourcePsStrategy();
        visitor = new PreparedStatementRenderer();
    }

    @Test
    void ok() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer_current", "id")))
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer_current", "name")))
                        .build())
                .from(From.fromTable("Customer_current"))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer_previous", "id")))
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Customer_previous", "name")))
                        .build())
                .from(From.fromTable("Customer_previous"))
                .build();
        InsertSource item = new InsertSource(UnionExpression.union(select1, select2));

        PreparedStatementSpec result = strategy.handle(item, visitor, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        """
            (\
            (SELECT \"Customer_current\".\"id\", \"Customer_current\".\"name\" FROM \"Customer_current\") \
            UNION \
            (SELECT \"Customer_previous\".\"id\", \"Customer_previous\".\"name\" FROM \"Customer_previous\")\
            )\
            """);
    }

    @Test
    void empty() {
        InsertSource item = new InsertSource(new NullSetExpression());
        PreparedStatementSpec result = strategy.handle(item, visitor, new AstContext());
        assertThat(result.sql()).isEqualTo("");
        assertThat(result.parameters()).isEmpty();
        ;
    }
}
