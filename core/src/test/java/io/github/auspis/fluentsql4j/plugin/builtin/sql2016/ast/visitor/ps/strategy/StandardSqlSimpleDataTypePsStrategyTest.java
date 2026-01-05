package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.ddl.definition.DataType.SimpleDataType;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.SimpleDataTypePsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlSimpleDataTypePsStrategyTest {

    private final SimpleDataTypePsStrategy strategy = new StandardSqlSimpleDataTypePsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void integerType() {
        SimpleDataType dataType = new SimpleDataType("INTEGER");

        PreparedStatementSpec result = strategy.handle(dataType, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("INTEGER");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void varcharType() {
        SimpleDataType dataType = new SimpleDataType("VARCHAR");

        PreparedStatementSpec result = strategy.handle(dataType, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("VARCHAR");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void textType() {
        SimpleDataType dataType = new SimpleDataType("TEXT");

        PreparedStatementSpec result = strategy.handle(dataType, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("TEXT");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void dateType() {
        SimpleDataType dataType = new SimpleDataType("DATE");

        PreparedStatementSpec result = strategy.handle(dataType, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("DATE");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void timestampType() {
        SimpleDataType dataType = new SimpleDataType("TIMESTAMP");

        PreparedStatementSpec result = strategy.handle(dataType, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("TIMESTAMP");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void customType() {
        SimpleDataType dataType = new SimpleDataType("CUSTOM_TYPE");

        PreparedStatementSpec result = strategy.handle(dataType, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("CUSTOM_TYPE");
        assertThat(result.parameters()).isEmpty();
    }
}
