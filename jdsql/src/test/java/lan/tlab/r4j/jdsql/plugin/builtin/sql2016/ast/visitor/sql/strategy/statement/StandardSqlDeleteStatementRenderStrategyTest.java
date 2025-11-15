package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.dml.statement.DeleteStatement;
import lan.tlab.r4j.sql.ast.dql.clause.Where;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement.StandardSqlDeleteStatementRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlDeleteStatementRenderStrategyTest {

    private StandardSqlDeleteStatementRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlDeleteStatementRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void ok() {
        DeleteStatement statement = DeleteStatement.builder()
                .table(new TableIdentifier("Customer"))
                .where(Where.of(Comparison.eq(ColumnReference.of("Customer", "status"), Literal.of("inactive"))))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
    		DELETE FROM \"Customer\" \
    		WHERE \"Customer\".\"status\" = 'inactive'\
    		""");
    }

    @Test
    void noWhere() {
        DeleteStatement statement =
                DeleteStatement.builder().table(new TableIdentifier("Customer")).build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql).isEqualTo("DELETE FROM \"Customer\"");
    }
}
