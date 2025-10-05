package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultColumnDefinitionPsStrategyTest {

    private final DefaultColumnDefinitionPsStrategy strategy = new DefaultColumnDefinitionPsStrategy();
    private final PreparedStatementVisitor visitor =
            PreparedStatementVisitor.builder().build();
    private final AstContext context = new AstContext();

    @Test
    void shouldHandleNullObject() {
        ColumnDefinition nullColumnDefinition = ColumnDefinition.nullObject();

        PsDto result = strategy.handle(nullColumnDefinition, visitor, context);

        Assertions.assertThat(result.sql()).isEmpty();
        Assertions.assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleSimpleColumn() {
        ColumnDefinition columnDefinition = ColumnDefinition.builder()
                .name("id")
                .type(new SimpleDataType("INTEGER"))
                .build();

        PsDto result = strategy.handle(columnDefinition, visitor, context);

        Assertions.assertThat(result.sql()).isEqualTo("\"id\" INTEGER");
        Assertions.assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleColumnWithDifferentDataType() {
        ColumnDefinition columnDefinition = ColumnDefinition.builder()
                .name("name")
                .type(new SimpleDataType("VARCHAR"))
                .build();

        PsDto result = strategy.handle(columnDefinition, visitor, context);

        Assertions.assertThat(result.sql()).isEqualTo("\"name\" VARCHAR");
        Assertions.assertThat(result.parameters()).isEmpty();
    }
}
