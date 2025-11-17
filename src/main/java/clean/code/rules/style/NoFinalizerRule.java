package clean.code.rules.style;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import clean.code.rules.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NoFinalizerRule implements Rule {

    private static final String RULE_ID = "NoFinalizer";
    private static final String MESSAGE = "Object.finalize() 메서드를 오버라이드하지 마세요. (Google Style Guide 6.4)";
    private final Severity severity;

    public NoFinalizerRule(Severity severity) {
        this.severity = severity;
    }

    @Override
    public Severity getSeverity() {
        return this.severity;
    }

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new FinalizerVisitor(filePath, severity), violations);
        return violations;
    }

    private static class FinalizerVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;
        private final Severity severity;

        public FinalizerVisitor(Path filePath, Severity severity) {
            this.filePath = filePath;
            this.severity = severity;
        }

        @Override
        public void visit(MethodDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            if (n.getNameAsString().equals("finalize") && n.getParameters().isEmpty()) {
                int line = n.getName().getRange()
                        .map(r -> r.begin.line)
                        .orElse(n.getRange().map(r -> r.begin.line).orElse(1));
                collector.add(new Violation(filePath, line, RULE_ID, MESSAGE, severity));
            }
        }
    }
}