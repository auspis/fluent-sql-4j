package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Comparison;
import io.github.massimiliano.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedUpdate;
import io.github.massimiliano.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlWhenMatchedUpdatePsStrategyTest {

    private StandardSqlWhenMatchedUpdatePsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlWhenMatchedUpdatePsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void withoutCondition() {
        List<UpdateItem> updateItems = List.of(
                UpdateItem.of("name", Literal.of("Updated Name")), UpdateItem.of("status", Literal.of("active")));
        WhenMatchedUpdate action = new WhenMatchedUpdate(null, updateItems);

        PreparedStatementSpec result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql()).isEqualTo("WHEN MATCHED THEN UPDATE SET \"name\" = ?, \"status\" = ?");
        assertThat(result.parameters()).containsExactly("Updated Name", "active");
    }

    @Test
    void withCondition() {
        List<UpdateItem> updateItems = List.of(UpdateItem.of("price", Literal.of(99.99)));
        Comparison condition = Comparison.gt(ColumnReference.of("target", "price"), Literal.of(50.0));
        WhenMatchedUpdate action = new WhenMatchedUpdate(condition, updateItems);

        PreparedStatementSpec result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql()).isEqualTo("WHEN MATCHED AND \"price\" > ? THEN UPDATE SET \"price\" = ?");
        assertThat(result.parameters()).containsExactly(50.0, 99.99);
    }

    @Test
    void multipleUpdateItems() {
        List<UpdateItem> updateItems = List.of(
                UpdateItem.of("col1", Literal.of("value1")),
                UpdateItem.of("col2", Literal.of(100)),
                UpdateItem.of("col3", Literal.of(true)));
        WhenMatchedUpdate action = new WhenMatchedUpdate(null, updateItems);

        PreparedStatementSpec result = strategy.handle(action, visitor, ctx);

        assertThat(result.sql()).isEqualTo("WHEN MATCHED THEN UPDATE SET \"col1\" = ?, \"col2\" = ?, \"col3\" = ?");
        assertThat(result.parameters()).containsExactly("value1", 100, true);
    }
}
