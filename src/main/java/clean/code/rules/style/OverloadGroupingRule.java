package clean.code.rules.style;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * [Style] 오버로드된 메서드/생성자는 연속된 그룹으로 모여있어야 함
 */
public class OverloadGroupingRule implements Rule {

    private static final String RULE_ID = "OverloadGrouping";

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new OverloadVisitor(filePath), violations);
        return violations;
    }

    private static class OverloadVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;

        public OverloadVisitor(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            Set<String> membersSeenSoFar = new HashSet<>();
            String lastMemberName = null;

            for (BodyDeclaration<?> member : n.getMembers()) {
                String currentMemberName = null;
                int currentLine = member.getRange().map(r -> r.begin.line).orElse(1);
                if (member instanceof MethodDeclaration) {
                    currentMemberName = ((MethodDeclaration) member).getNameAsString();
                } else if (member instanceof ConstructorDeclaration) {
                    currentMemberName = n.getNameAsString();
                }
                if (currentMemberName != null) {
                    if (currentMemberName.equals(lastMemberName)) {
                    } else {
                        if (membersSeenSoFar.contains(currentMemberName)) {
                            String message = String.format(
                                    "오버로드된 '%s'이(가) 이전에 나타났지만, 다른 멤버에 의해 분리되었습니다.",
                                    currentMemberName
                            );
                            collector.add(new Violation(filePath, currentLine, RULE_ID, message));
                        }
                        membersSeenSoFar.add(currentMemberName);
                        lastMemberName = currentMemberName;
                    }
                } else {
                    lastMemberName = null;
                }
            }
        }
    }
}