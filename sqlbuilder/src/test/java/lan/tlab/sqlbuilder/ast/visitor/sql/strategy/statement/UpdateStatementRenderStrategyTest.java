package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.item.UpdateItem;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.sqlbuilder.ast.statement.UpdateStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateStatementRenderStrategyTest {

    private UpdateStatementRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new UpdateStatementRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void valuesAndFunctions() {
        UpdateStatement statement = UpdateStatement.builder()
                .table(new Table("users"))
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
                .table(new Table("users"))
                .set(List.of(UpdateItem.of("status", Literal.of("active"))))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql).isEqualTo("UPDATE \"users\" SET \"status\" = 'active'");
    }
}
