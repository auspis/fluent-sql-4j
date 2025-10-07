package lan.tlab.r4j.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import org.junit.jupiter.api.Test;

class ColumnReferenceUtilTest {

    @Test
    void parseSimpleColumnWithDefaultTableReference() {
        ColumnReference result = ColumnReferenceUtil.parseColumnReference("customer_id", "orders");

        assertThat(result.getTable()).isEqualTo("orders");
        assertThat(result.getColumn()).isEqualTo("customer_id");
    }

    @Test
    void parseQualifiedColumn() {
        ColumnReference result = ColumnReferenceUtil.parseColumnReference("orders.customer_id", "");

        assertThat(result.getTable()).isEqualTo("orders");
        assertThat(result.getColumn()).isEqualTo("customer_id");
    }

    @Test
    void parseQualifiedColumnIgnoresDefaultTableReference() {
        ColumnReference result = ColumnReferenceUtil.parseColumnReference("o.customer_id", "orders");

        assertThat(result.getTable()).isEqualTo("o");
        assertThat(result.getColumn()).isEqualTo("customer_id");
    }

    @Test
    void parseColumnWithEmptyDefaultTableReference() {
        ColumnReference result = ColumnReferenceUtil.parseColumnReference("id", "");

        assertThat(result.getTable()).isEqualTo("");
        assertThat(result.getColumn()).isEqualTo("id");
    }
}
