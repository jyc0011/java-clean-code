package clean.code.rules.cleancode;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * [클린코드] 3개 이상의 인스턴스 변수 제한 규칙
 */
public class InstanceVarCountRule implements Rule {

    private static final String RULE_ID = "InstanceVarCount";
    private final int maxCount;

    public InstanceVarCountRule(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new ClassVisitor(filePath, maxCount), violations);
        return violations;
    }

    private static class ClassVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;
        private final int maxCount;

        public ClassVisitor(Path filePath, int maxCount) {
            this.filePath = filePath;
            this.maxCount = maxCount;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            long instanceVarCount = n.getFields().stream()
                    .filter(field -> !field.isStatic())
                    .mapToLong(field -> field.getVariables().size())
                    .sum();

            if (instanceVarCount > maxCount) {
                String message = String.format(
                        "클래스에 인스턴스 변수가 %d개입니다. (허용 기준: %d개).",
                        instanceVarCount, maxCount
                );
                collector.add(new Violation(filePath, n.getRange().map(r -> r.begin.line).orElse(1), RULE_ID, message));
            }
        }
    }
}