package clean.code.rules.style;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * [Style] Object.finalize 메서드 오버라이드 금지 (Google Style Guide: 6.4 Finalizers: not used)
 */
public class NoFinalizerRule implements Rule {

    private static final String RULE_ID = "NoFinalizer";
    private static final String MESSAGE = "Object.finalize() 메서드를 오버라이드하지 마세요. (Google Style Guide 6.4)";

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new FinalizerVisitor(filePath), violations);
        return violations;
    }

    private static class FinalizerVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;

        public FinalizerVisitor(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public void visit(MethodDeclaration n, List<Violation> collector) {
            super.visit(n, collector);

            if (n.getNameAsString().equals("finalize") && n.getParameters().isEmpty()) {
                int line = n.getName().getRange()
                        .map(r -> r.begin.line)
                        .orElse(n.getRange().map(r -> r.begin.line).orElse(1)); // 이름 범위를 못찾을 경우 fallback

                collector.add(new Violation(filePath, line, RULE_ID, MESSAGE));
            }
        }
    }
}