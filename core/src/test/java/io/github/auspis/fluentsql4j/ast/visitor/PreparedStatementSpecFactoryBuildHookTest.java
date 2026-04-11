package io.github.auspis.fluentsql4j.ast.visitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.auspis.fluentsql4j.ast.core.statement.Statement;
import io.github.auspis.fluentsql4j.ast.dql.clause.From;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.hook.build.BuildHook;
import io.github.auspis.fluentsql4j.hook.build.BuildHookFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PreparedStatementSpecFactoryBuildHookTest {

    private Statement selectStatement;
    private List<String> events;
    private BuildHookFactory hookFactory;

    @BeforeEach
    void setUp() {
        selectStatement =
                SelectStatement.builder().from(From.fromTable("users")).build();
        events = new ArrayList<>();
        hookFactory = () -> new TestBuildHook(events);
    }

    @Test
    void ok() {
        PreparedStatementSpec expectedSpec = new PreparedStatementSpec();
        AstToPreparedStatementSpecVisitor astVisitor = new SuccessVisitor(expectedSpec);

        PreparedStatementSpecFactory factory = new PreparedStatementSpecFactory(astVisitor, hookFactory);

        PreparedStatementSpec actualSpec = factory.create(selectStatement);

        assertThat(actualSpec).isEqualTo(expectedSpec);
        assertThat(events).containsExactly("before", "success");
    }

    @Test
    void error() {
        RuntimeException expected = new RuntimeException("build-fail");
        AstToPreparedStatementSpecVisitor astVisitor = new FailingVisitor(expected);

        PreparedStatementSpecFactory factory = new PreparedStatementSpecFactory(astVisitor, hookFactory);

        assertThatThrownBy(() -> factory.create(selectStatement)).isSameAs(expected);
        assertThat(events).containsExactly("before", "error");
    }

    private static class SuccessVisitor extends AstToPreparedStatementSpecVisitor {
        private final PreparedStatementSpec expectedSpec;

        private SuccessVisitor(PreparedStatementSpec expectedSpec) {
            this.expectedSpec = expectedSpec;
        }

        @Override
        public PreparedStatementSpec visit(SelectStatement clause, AstContext ctx) {
            return expectedSpec;
        }
    }

    private static class FailingVisitor extends AstToPreparedStatementSpecVisitor {
        private final RuntimeException expected;

        private FailingVisitor(RuntimeException expected) {
            this.expected = expected;
        }

        @Override
        public PreparedStatementSpec visit(SelectStatement clause, AstContext ctx) {
            throw expected;
        }
    }

    @SuppressWarnings("unused")
    private static class TestBuildHook extends BuildHook {
        private final List<String> events;

        public TestBuildHook(List<String> events) {
            this.events = events;
        }

        public List<String> getEvents() {
            return events;
        }

        @Override
        protected void doBefore(Statement ignored) {
            events.add("before");
        }

        @Override
        protected void doOnSuccess(PreparedStatementSpec ignored) {
            events.add("success");
        }

        @Override
        protected void doOnError(Throwable error) {
            events.add("error");
        }
    }
}
