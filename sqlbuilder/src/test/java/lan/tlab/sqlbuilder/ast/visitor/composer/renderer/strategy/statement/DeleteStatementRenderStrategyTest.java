package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.statement.DeleteStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteStatementRenderStrategyTest {

    private DeleteStatementRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new DeleteStatementRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        DeleteStatement statement = DeleteStatement.builder()
                .table(new Table("Customer"))
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
                DeleteStatement.builder().table(new Table("Customer")).build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql).isEqualTo("DELETE FROM \"Customer\"");
    }
}
