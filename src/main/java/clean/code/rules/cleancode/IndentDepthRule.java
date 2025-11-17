package clean.code.rules.cleancode;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * [클린코드] 메서드 내 들여쓰기 깊이 제한 (Max 2)
 */
public class IndentDepthRule implements Rule {

    private static final String RULE_ID = "IndentDepth";
    private final int maxDepth;
    private final int indentSize;

    public IndentDepthRule(int maxDepth, int indentSize) {
        this.maxDepth = maxDepth;
        this.indentSize = indentSize;
    }

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new MethodVisitor(filePath, maxDepth, indentSize), violations);
        return violations;
    }

    private static class MethodVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;
        private final int maxDepth;
        private final int indentSize;

        public MethodVisitor(Path filePath, int maxDepth, int indentSize) {
            this.filePath = filePath;
            this.maxDepth = maxDepth;
            this.indentSize = indentSize;
        }

        @Override
        public void visit(MethodDeclaration n, List<Violation> collector) {
            n.getBody().ifPresent(body -> {
                int baseIndent = n.getRange().map(r -> r.begin.column).orElse(0);
                StatementVisitor statementVisitor = new StatementVisitor(filePath, maxDepth, indentSize, baseIndent);
                body.accept(statementVisitor, collector);
            });
        }
    }

    private static class StatementVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;
        private final int maxDepth;
        private final int indentSize;
        private final int baseIndent;

        public StatementVisitor(Path filePath, int maxDepth, int indentSize, int baseIndent) {
            this.filePath = filePath;
            this.maxDepth = maxDepth;
            this.indentSize = indentSize;
            this.baseIndent = baseIndent;
        }

        @Override
        public void visit(IfStmt n, List<Violation> collector) {
            check(n, collector);
            super.visit(n, collector);
        }

        @Override
        public void visit(ForStmt n, List<Violation> collector) {
            check(n, collector);
            super.visit(n, collector);
        }

        @Override
        public void visit(ForEachStmt n, List<Violation> collector) {
            check(n, collector);
            super.visit(n, collector);
        }

        @Override
        public void visit(WhileStmt n, List<Violation> collector) {
            check(n, collector);
            super.visit(n, collector);
        }

        @Override
        public void visit(DoStmt n, List<Violation> collector) {
            check(n, collector);
            super.visit(n, collector);
        }

        @Override
        public void visit(SwitchStmt n, List<Violation> collector) {
            check(n, collector);
            super.visit(n, collector);
        }

        @Override
        public void visit(TryStmt n, List<Violation> collector) {
            check(n, collector);
            super.visit(n, collector);
        }

        @Override
        public void visit(SynchronizedStmt n, List<Violation> collector) {
            check(n, collector);
            super.visit(n, collector);
        }

        private void check(Node node, List<Violation> collector) {
            int nodeIndent = node.getRange().map(r -> r.begin.column).orElse(0);
            int relativeIndent = nodeIndent - baseIndent;
            int depth = relativeIndent / indentSize;

            if (depth > maxDepth) {
                int line = node.getRange().map(r -> r.begin.line).orElse(1);
                String message = String.format(
                        "들여쓰기 깊이가 %d입니다. (허용 기준: %d). 메서드 분리를 고려하세요.",
                        depth, maxDepth
                );
                collector.add(new Violation(filePath, line, RULE_ID, message));
            }
        }
    }
}