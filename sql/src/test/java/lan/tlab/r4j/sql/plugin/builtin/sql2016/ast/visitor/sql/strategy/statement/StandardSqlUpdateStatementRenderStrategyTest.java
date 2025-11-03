package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.dml.UpdateStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlUpdateStatementRenderStrategyTest {

    private StandardSqlUpdateStatementRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlUpdateStatementRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void valuesAndFunctions() {
        UpdateStatement statement = UpdateStatement.builder()
                .table(new TableIdentifier("users"))
                .set(List.of(
                        UpdateItem.of("status", Literal.of("active")),
                        UpdateItem.of("updatedAt", new CurrentDateTime())))
                .where(Where.of(Comparison.eq(ColumnReference.of("Customer", "id"), Literal.of(123))))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
    		UPDATE \"users\" \
    		SET \"status\" = 'active', \"updatedAt\" = CURRENT_TIMESTAMP() \
    		WHERE \"Customer\".\"id\" = 123\
    		""");
    }

    @Test
    void noWhere() {
        UpdateStatement statement = UpdateStatement.builder()
                .table(new TableIdentifier("users"))
                .set(List.of(UpdateItem.of("status", Literal.of("active"))))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql).isEqualTo("UPDATE \"users\" SET \"status\" = 'active'");
    }
}
