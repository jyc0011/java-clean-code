package clean.code.rules.cleancode;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import clean.code.rules.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NoHardcodingRule implements Rule {

    private static final String RULE_ID = "NoHardcoding";
    private static final Set<String> ALLOWED_NUMBERS = Set.of("0", "1", "-1");
    private final Severity severity;

    public NoHardcodingRule(Severity severity) {
        this.severity = severity;
    }

    @Override
    public Severity getSeverity() {
        return this.severity;
    }

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new HardcodingVisitor(filePath, severity), violations);
        return violations;
    }

    private static class HardcodingVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;
        private final Severity severity;

        public HardcodingVisitor(Path filePath, Severity severity) {
            this.filePath = filePath;
            this.severity = severity;
        }

        @Override
        public void visit(StringLiteralExpr n, List<Violation> collector) {
            super.visit(n, collector);
            if (!n.getValue().isEmpty() && !isConstantOrAnnotation(n)) {
                addViolation(n, "\"" + n.getValue() + "\"", collector);
            }
        }

        @Override
        public void visit(IntegerLiteralExpr n, List<Violation> collector) {
            super.visit(n, collector);
            if (!ALLOWED_NUMBERS.contains(n.getValue()) && !isConstantOrAnnotation(n)) {
                addViolation(n, n.getValue(), collector);
            }
        }

        private boolean isConstantOrAnnotation(Node node) {
            Node parent = node.getParentNode().orElse(null);
            while (parent != null) {
                if (parent instanceof FieldDeclaration field) {
                    if (field.isStatic() && field.isFinal()) {
                        return true;
                    }
                }
                if (parent instanceof AnnotationExpr) {
                    return true;
                }
                parent = parent.getParentNode().orElse(null);
            }
            return false;
        }

        private void addViolation(LiteralExpr n, String value, List<Violation> collector) {
            int line = n.getRange().map(r -> r.begin.line).orElse(1);
            String message = String.format(
                    "하드코딩된 값(%s)이 있습니다. 'static final' 상수로 분리하세요.",
                    value
            );
            collector.add(new Violation(filePath, line, RULE_ID, message, severity));
        }
    }
}