package clean.code.rules.cleancode;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * [클린코드] 디미터 법칙 (점 개수 제한)
 */
public class LawOfDemeterRule implements Rule {

    private static final String RULE_ID = "LawOfDemeter";
    private final int maxDots;

    public LawOfDemeterRule(int maxDots) {
        this.maxDots = maxDots;
    }

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new DotVisitor(filePath, maxDots), violations);
        return violations;
    }

    private static class DotVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;
        private final int maxDots;

        public DotVisitor(Path filePath, int maxDots) {
            this.filePath = filePath;
            this.maxDots = maxDots;
        }

        @Override
        public void visit(MethodCallExpr n, List<Violation> collector) {
            super.visit(n, collector);
            n.getScope().ifPresent(scope -> checkDotCount(n, scope, collector));
        }

        @Override
        public void visit(FieldAccessExpr n, List<Violation> collector) {
            super.visit(n, collector);
            checkDotCount(n, n.getScope(), collector);
        }

        private void checkDotCount(Node originalNode, Expression scope, List<Violation> collector) {
            int dotCount = 0;
            Expression currentScope = scope;

            // 'a.b.c' -> scope는 'a.b'
            while (currentScope != null) {
                dotCount++;

                if (currentScope instanceof MethodCallExpr) {
                    currentScope = ((MethodCallExpr) currentScope).getScope().orElse(null);
                } else if (currentScope instanceof FieldAccessExpr) {
                    currentScope = ((FieldAccessExpr) currentScope).getScope();
                } else {
                    currentScope = null;
                }
            }

            if (dotCount > maxDots) {
                int line = originalNode.getRange().map(r -> r.begin.line).orElse(1);
                String message = String.format(
                        "한 줄에 점(.)이 %d개 사용되었습니다. (허용 기준: %d개). 디미터 법칙 위반 가능성이 있습니다.",
                        dotCount, maxDots
                );
                collector.add(new Violation(filePath, line, RULE_ID, message));
            }
        }
    }
}