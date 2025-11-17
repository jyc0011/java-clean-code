package clean.code.rules.oop;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import clean.code.rules.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WrapPrimitiveRule implements Rule {

    private static final String RULE_ID = "WrapPrimitive";
    private static final String MESSAGE = "메서드(생성자) 인자에 원시값(primitive/String)이 %d개 있습니다. (허용 기준: %d개). " +
                                          "의미있는 객체(e.g., Age, Name)로 포장하는 것을 고려하세요.";

    private final int maxPrimitives;
    private final Severity severity;

    public WrapPrimitiveRule(int maxPrimitives, Severity severity) {
        this.maxPrimitives = maxPrimitives;
        this.severity = severity;
    }

    @Override
    public Severity getSeverity() {
        return this.severity;
    }

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new PrimitiveVisitor(filePath, maxPrimitives, severity), violations);
        return violations;
    }

    private static class PrimitiveVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;
        private final int maxPrimitives;
        private final Severity severity;

        public PrimitiveVisitor(Path filePath, int maxPrimitives, Severity severity) {
            this.filePath = filePath;
            this.maxPrimitives = maxPrimitives;
            this.severity = severity;
        }

        @Override
        public void visit(MethodDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            checkParameters(n.getParameters(), n.getRange().map(r -> r.begin.line).orElse(1), collector);
        }

        @Override
        public void visit(ConstructorDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            checkParameters(n.getParameters(), n.getRange().map(r -> r.begin.line).orElse(1), collector);
        }

        private void checkParameters(List<Parameter> parameters, int line, List<Violation> collector) {
            long primitiveCount = parameters.stream()
                    .map(Parameter::getType)
                    .filter(this::isPrimitiveOrString)
                    .count();

            if (primitiveCount >= maxPrimitives) {
                collector.add(new Violation(filePath, line, RULE_ID,
                        String.format(MESSAGE, primitiveCount, maxPrimitives - 1),
                        severity
                ));
            }
        }

        private boolean isPrimitiveOrString(Type type) {
            return type.isPrimitiveType() || type.asString().equals("String");
        }
    }
}