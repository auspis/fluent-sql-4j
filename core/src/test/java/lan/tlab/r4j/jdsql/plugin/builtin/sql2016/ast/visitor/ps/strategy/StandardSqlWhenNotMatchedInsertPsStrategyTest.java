package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertValues;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlWhenNotMatchedInsertPsStrategyTest {

    private StandardSqlWhenNotMatchedInsertPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlWhenNotMatchedInsertPsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void withoutConditionOrColumns() {
        InsertValues insertData = InsertValues.of(Literal.of("New Name"), Literal.of(100));
        WhenNotMatchedInsert action = new WhenNotMatchedInsert(null, List.of(), insertData);

        PreparedStatementSpec result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql()).isEqualTo("WHEN NOT MATCHED THEN INSERT ?, ?");
        assertThat(result.parameters()).containsExactly("New Name", 100);
    }

    @Test
    void withColumns() {
        List<ColumnReference> columns = List.of(ColumnReference.of("", "name"), ColumnReference.of("", "amount"));
        InsertValues insertData = InsertValues.of(Literal.of("Item"), Literal.of(250));
        WhenNotMatchedInsert action = new WhenNotMatchedInsert(null, columns, insertData);

        PreparedStatementSpec result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql()).isEqualTo("WHEN NOT MATCHED THEN INSERT (\"name\", \"amount\") ?, ?");
        assertThat(result.parameters()).containsExactly("Item", 250);
    }

    @Test
    void withCondition() {
        Comparison condition = Comparison.gt(ColumnReference.of("source", "priority"), Literal.of(5));
        InsertValues insertData = InsertValues.of(Literal.of("High Priority"));
        WhenNotMatchedInsert action = new WhenNotMatchedInsert(condition, List.of(), insertData);

        PreparedStatementSpec result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql()).isEqualTo("WHEN NOT MATCHED AND \"priority\" > ? THEN INSERT ?");
        assertThat(result.parameters()).containsExactly(5, "High Priority");
    }

    @Test
    void withConditionAndColumns() {
        Comparison condition = Comparison.eq(ColumnReference.of("source", "type"), Literal.of("new"));
        List<ColumnReference> columns =
                List.of(ColumnReference.of("", "id"), ColumnReference.of("", "name"), ColumnReference.of("", "value"));
        InsertValues insertData = InsertValues.of(Literal.of(999), Literal.of("Test"), Literal.of(42.5));
        WhenNotMatchedInsert action = new WhenNotMatchedInsert(condition, columns, insertData);

        PreparedStatementSpec result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql())
                .isEqualTo("WHEN NOT MATCHED AND \"type\" = ? THEN INSERT (\"id\", \"name\", \"value\") ?, ?, ?");
        assertThat(result.parameters()).containsExactly("new", 999, "Test", 42.5);
    }
}
