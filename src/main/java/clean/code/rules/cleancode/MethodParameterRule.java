package clean.code.rules.cleancode;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * [클린코드] 메서드 인자 수 제한 (4개 이상)
 */
public class MethodParameterRule implements Rule {

    private static final String RULE_ID = "MethodParameter";
    private final int maxParameters;

    public MethodParameterRule(int maxParameters) {
        this.maxParameters = maxParameters;
    }

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new ParameterVisitor(filePath, maxParameters), violations);
        return violations;
    }

    private static class ParameterVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;
        private final int maxParameters;

        public ParameterVisitor(Path filePath, int maxParameters) {
            this.filePath = filePath;
            this.maxParameters = maxParameters;
        }

        @Override
        public void visit(MethodDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            checkParameters(n.getParameters().size(), n.getBegin().map(p -> p.line).orElse(1), collector);
        }

        @Override
        public void visit(ConstructorDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            checkParameters(n.getParameters().size(), n.getBegin().map(p -> p.line).orElse(1), collector);
        }

        private void checkParameters(int paramCount, int line, List<Violation> collector) {
            if (paramCount > maxParameters) {
                String message = String.format(
                        "메서드(생성자) 인자가 %d개입니다. (허용 기준: %d개). 인자를 객체로 포장하는 것을 고려하세요.",
                        paramCount, maxParameters
                );
                collector.add(new Violation(filePath, line, RULE_ID, message));
            }
        }
    }
}