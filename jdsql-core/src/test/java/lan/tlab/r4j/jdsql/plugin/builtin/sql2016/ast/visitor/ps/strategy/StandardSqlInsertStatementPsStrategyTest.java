package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.Expression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.DefaultValues;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertValues;
import lan.tlab.r4j.jdsql.ast.dml.statement.InsertStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlInsertStatementPsStrategyTest {

    private StandardSqlInsertStatementPsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlInsertStatementPsStrategy();
        visitor = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void insertWithDefaultValues() {
        TableIdentifier table = new TableIdentifier("User");
        DefaultValues defaultValues = new DefaultValues();
        InsertStatement stmt =
                InsertStatement.builder().table(table).data(defaultValues).build();

        PsDto result = strategy.handle(stmt, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INSERT INTO \"User\" DEFAULT VALUES");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void insertWithSingleColumn() {
        TableIdentifier table = new TableIdentifier("User");
        List<ColumnReference> columns = List.of(ColumnReference.of("User", "name"));
        List<Expression> values = List.of(Literal.of("John"));
        InsertValues insertValues = new InsertValues(values);

        InsertStatement stmt = InsertStatement.builder()
                .table(table)
                .columns(columns)
                .data(insertValues)
                .build();

        PsDto result = strategy.handle(stmt, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INSERT INTO \"User\" (\"name\") VALUES (?)");
        assertThat(result.parameters()).containsExactly("John");
    }

    @Test
    void insertWitthMultipleColumns() {
        TableIdentifier table = new TableIdentifier("User");
        List<ColumnReference> columns = List.of(
                ColumnReference.of("User", "id"),
                ColumnReference.of("User", "name"),
                ColumnReference.of("User", "email"));
        List<Expression> values = List.of(Literal.of(1), Literal.of("John"), Literal.of("john@example.com"));
        InsertValues insertValues = new InsertValues(values);

        InsertStatement stmt = InsertStatement.builder()
                .table(table)
                .columns(columns)
                .data(insertValues)
                .build();

        PsDto result = strategy.handle(stmt, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INSERT INTO \"User\" (\"id\", \"name\", \"email\") VALUES (?, ?, ?)");
        assertThat(result.parameters()).containsExactly(1, "John", "john@example.com");
    }

    @Test
    void insertWithMixedTypes() {
        TableIdentifier table = new TableIdentifier("Product");
        List<ColumnReference> columns = List.of(
                ColumnReference.of("Product", "id"),
                ColumnReference.of("Product", "name"),
                ColumnReference.of("Product", "price"),
                ColumnReference.of("Product", "active"));
        List<Expression> values = List.of(Literal.of(123), Literal.of("Widget"), Literal.of(29.99), Literal.of(true));
        InsertValues insertValues = new InsertValues(values);

        InsertStatement stmt = InsertStatement.builder()
                .table(table)
                .columns(columns)
                .data(insertValues)
                .build();

        PsDto result = strategy.handle(stmt, visitor, ctx);

        assertThat(result.sql())
                .isEqualTo("INSERT INTO \"Product\" (\"id\", \"name\", \"price\", \"active\") VALUES (?, ?, ?, ?)");
        assertThat(result.parameters()).containsExactly(123, "Widget", 29.99, true);
    }

    @Test
    void insertWithTableNameSpecialCharacters() {
        TableIdentifier table = new TableIdentifier("order_items");
        List<ColumnReference> columns = List.of(ColumnReference.of("order_items", "item_id"));
        List<Expression> values = List.of(Literal.of(42));
        InsertValues insertValues = new InsertValues(values);

        InsertStatement stmt = InsertStatement.builder()
                .table(table)
                .columns(columns)
                .data(insertValues)
                .build();

        PsDto result = strategy.handle(stmt, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INSERT INTO \"order_items\" (\"item_id\") VALUES (?)");
        assertThat(result.parameters()).containsExactly(42);
    }
}
