package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDateTime;
import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.dml.statement.UpdateStatement;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
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
