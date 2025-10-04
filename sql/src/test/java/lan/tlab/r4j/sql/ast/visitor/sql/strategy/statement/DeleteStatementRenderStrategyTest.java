package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.expression.bool.Comparison;
import lan.tlab.r4j.sql.ast.expression.item.Table;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.statement.DeleteStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
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
