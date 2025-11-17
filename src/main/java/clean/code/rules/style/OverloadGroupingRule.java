package clean.code.rules.style;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import clean.code.rules.Severity;
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

public class OverloadGroupingRule implements Rule {

    private static final String RULE_ID = "OverloadGrouping";
    private final Severity severity;

    public OverloadGroupingRule(Severity severity) {
        this.severity = severity;
    }

    @Override
    public Severity getSeverity() {
        return this.severity;
    }

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new OverloadVisitor(filePath, severity), violations);
        return violations;
    }

    private static class OverloadVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;
        private final Severity severity;

        public OverloadVisitor(Path filePath, Severity severity) {
            this.filePath = filePath;
            this.severity = severity;
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
                        // OK
                    } else {
                        if (membersSeenSoFar.contains(currentMemberName)) {
                            String message = String.format(
                                    "오버로드된 '%s'이(가) 이전에 나타났지만, 다른 멤버에 의해 분리되었습니다.",
                                    currentMemberName
                            );
                            collector.add(new Violation(filePath, currentLine, RULE_ID, message, severity));
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