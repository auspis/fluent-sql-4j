package lan.tlab.r4j.jdsql.dsl.util;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import org.junit.jupiter.api.Test;

class ColumnReferenceUtilTest {

    @Test
    void parseSimpleColumnWithDefaultTableReference() {
        ColumnReference result = ColumnReferenceUtil.parseColumnReference("customer_id", "orders");

        assertThat(result.table()).isEqualTo("orders");
        assertThat(result.column()).isEqualTo("customer_id");
    }

    @Test
    void parseQualifiedColumn() {
        ColumnReference result = ColumnReferenceUtil.parseColumnReference("orders.customer_id", "");

        assertThat(result.table()).isEqualTo("orders");
        assertThat(result.column()).isEqualTo("customer_id");
    }

    @Test
    void parseQualifiedColumnIgnoresDefaultTableReference() {
        ColumnReference result = ColumnReferenceUtil.parseColumnReference("o.customer_id", "orders");

        assertThat(result.table()).isEqualTo("o");
        assertThat(result.column()).isEqualTo("customer_id");
    }

    @Test
    void parseColumnWithEmptyDefaultTableReference() {
        ColumnReference result = ColumnReferenceUtil.parseColumnReference("id", "");

        assertThat(result.table()).isEqualTo("");
        assertThat(result.column()).isEqualTo("id");
    }
}
