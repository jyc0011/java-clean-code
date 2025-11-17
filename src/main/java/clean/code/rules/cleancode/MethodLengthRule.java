package clean.code.rules.cleancode;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import clean.code.rules.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/*
  [클린코드] 메서드 최대 길이 제한 규칙
 */
import clean.code.rules.Severity;


public class MethodLengthRule implements Rule {

    private static final String RULE_ID = "MethodLength";
    private final int maxLines;
    private final Severity severity;

    public MethodLengthRule(int maxLines, Severity severity) {
        this.maxLines = maxLines;
        this.severity = severity;
    }

    @Override
    public Severity getSeverity() {
        return this.severity;
    }

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new MethodLengthVisitor(filePath, maxLines, severity), violations);
        return violations;
    }

    private static class MethodLengthVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;
        private final int maxLines;
        private final Severity severity;

        public MethodLengthVisitor(Path filePath, int maxLines, Severity severity) {
            this.filePath = filePath;
            this.maxLines = maxLines;
            this.severity = severity;
        }

        @Override
        public void visit(MethodDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            n.getRange().ifPresent(range -> {
                int startLine = range.begin.line;
                int endLine = range.end.line;
                int length = (endLine - startLine) + 1;

                if (length <= maxLines) {
                    return;
                }

                String message = String.format(
                        "메서드 길이가 %d라인입니다. (허용 기준: %d라인). 한 가지 기능만 담당하도록 메서드를 더 작게 분리해보세요.",
                        length, maxLines
                );
                collector.add(new Violation(filePath, startLine, RULE_ID, message, severity));
            });
        }
    }
}