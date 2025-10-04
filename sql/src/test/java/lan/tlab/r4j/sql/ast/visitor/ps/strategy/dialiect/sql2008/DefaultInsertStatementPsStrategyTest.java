package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.expression.item.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.expression.item.InsertData.InsertValues;
import lan.tlab.r4j.sql.ast.expression.item.Table;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.statement.InsertStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultInsertStatementPsStrategyTest {

    private DefaultInsertStatementPsStrategy strategy;
    private PreparedStatementVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new DefaultInsertStatementPsStrategy();
        visitor = new PreparedStatementVisitor();
        ctx = new AstContext();
    }

    @Test
    void insertWithDefaultValues() {
        Table table = new Table("User");
        DefaultValues defaultValues = new DefaultValues();
        InsertStatement stmt =
                InsertStatement.builder().table(table).data(defaultValues).build();

        PsDto result = strategy.handle(stmt, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INSERT INTO \"User\" DEFAULT VALUES");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void insertWithSingleColumn() {
        Table table = new Table("User");
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
    void insertWithMultipleColumns() {
        Table table = new Table("User");
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
        Table table = new Table("Product");
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
        Table table = new Table("order_items");
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
