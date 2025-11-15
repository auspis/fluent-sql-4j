package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.sql.ast.common.expression.set.SetExpression;
import lan.tlab.r4j.sql.ast.common.expression.set.UnionExpression;
import lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.dml.component.InsertData.InsertSource;
import lan.tlab.r4j.sql.ast.dml.component.InsertData.InsertValues;
import lan.tlab.r4j.sql.ast.dml.statement.InsertStatement;
import lan.tlab.r4j.sql.ast.dql.clause.From;
import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement.StandardSqlInsertStatementRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlInsertStatementRenderStrategyTest {

    private StandardSqlInsertStatementRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlInsertStatementRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void ok() {
        InsertStatement statement = InsertStatement.builder()
                .table(new TableIdentifier("Logs"))
                .columns(List.of(
                        ColumnReference.of("Logs", "message"),
                        ColumnReference.of("Logs", "statusCode"),
                        ColumnReference.of("Logs", "createdAt")))
                .data(InsertValues.of(Literal.of("Success"), Literal.of(200), Literal.of(LocalDate.of(2025, 8, 28))))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());

        assertThat(sql)
                .isEqualTo(
                        """
			INSERT INTO \"Logs\" \
			(\"Logs\".\"message\", \"Logs\".\"statusCode\", \"Logs\".\"createdAt\") \
			VALUES ('Success', 200, '2025-08-28')\
			""");
    }

    @Test
    void noColumns() {
        InsertStatement statement = InsertStatement.builder()
                .table(new TableIdentifier("Logs"))
                .data(InsertValues.of(Literal.of("Success"), Literal.of(200), Literal.of(LocalDate.of(2025, 8, 28))))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());

        assertThat(sql).isEqualTo("""
			INSERT INTO \"Logs\" VALUES ('Success', 200, '2025-08-28')\
			""");
    }

    @Test
    void defaultValues() {
        InsertStatement statement =
                InsertStatement.builder().table(new TableIdentifier("Logs")).build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql).isEqualTo("INSERT INTO \"Logs\" DEFAULT VALUES");
    }

    @Test
    void valuesAndFunctions() {
        InsertStatement statement = InsertStatement.builder()
                .table(new TableIdentifier("Logs"))
                .columns(List.of(
                        ColumnReference.of("Logs", "message"),
                        ColumnReference.of("Logs", "statusCode"),
                        ColumnReference.of("Logs", "createdAt")))
                .data(InsertValues.of(Literal.of("Success"), Literal.of(200), new CurrentDate()))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());

        assertThat(sql)
                .isEqualTo(
                        """
			INSERT INTO \"Logs\" \
			(\"Logs\".\"message\", \"Logs\".\"statusCode\", \"Logs\".\"createdAt\") \
			VALUES ('Success', 200, CURRENT_DATE())\
			""");
    }

    @Test
    void fromSelect() {
        SelectStatement select = SelectStatement.builder()
                .select(Select.builder()
                        .projection(new ScalarExpressionProjection(ColumnReference.of("Logs", "message")))
                        .build())
                .from(From.fromTable("Logs"))
                .build();

        InsertStatement statement = InsertStatement.builder()
                .table(new TableIdentifier("Archive"))
                .data(new InsertSource(select))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo("""
			INSERT INTO \"Archive\" \
			SELECT \"Logs\".\"message\" FROM \"Logs\"\
			""");
    }

    @Test
    void fromSubquery() {
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

        SetExpression union = UnionExpression.union(select1, select2);

        InsertStatement statement = InsertStatement.builder()
                .table(new TableIdentifier("Archive"))
                .columns(List.of(ColumnReference.of("Archive", "id"), ColumnReference.of("Archive", "name")))
                .data(new InsertSource(union))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());

        assertThat(sql)
                .isEqualTo(
                        """
			INSERT INTO \"Archive\" (\"Archive\".\"id\", \"Archive\".\"name\") \
			(\
			(SELECT \"Customer_current\".\"id\", \"Customer_current\".\"name\" FROM \"Customer_current\") \
			UNION \
			(SELECT \"Customer_previous\".\"id\", \"Customer_previous\".\"name\" FROM \"Customer_previous\")\
			)\
			""");
    }

    @Test
    void insertWithSpecialTableName() {
        InsertStatement statement = InsertStatement.builder()
                .table(new TableIdentifier("Order"))
                .data(InsertValues.of(Literal.of("Test")))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql).isEqualTo("INSERT INTO \"Order\" VALUES ('Test')");
    }

    @Test
    void specialColumnNames() {
        InsertStatement statement = InsertStatement.builder()
                .table(new TableIdentifier("Logs"))
                .columns(List.of(ColumnReference.of("Logs", "select"), ColumnReference.of("Logs", "from")))
                .data(InsertValues.of(Literal.of("A"), Literal.of("B")))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
			INSERT INTO \"Logs\" \
			(\"Logs\".\"select\", \"Logs\".\"from\") \
			VALUES ('A', 'B')\
			""");
    }
}
