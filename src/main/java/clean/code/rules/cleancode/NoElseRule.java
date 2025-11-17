package clean.code.rules.cleancode;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * [클린코드] 'else' 예약어 사용을 지양하는 규칙 (getElseStmt() 사용 버전)
 */
public class NoElseRule implements Rule {

    private static final String RULE_ID = "NoElse";
    private static final String MESSAGE = "The 'else' keyword is discouraged. Use early returns (guard clauses) instead.";

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new IfStmtVisitor(filePath), violations);
        return violations;
    }

    /**
     * 'IfStmt' 노드만 방문하는 Visitor
     */
    private static class IfStmtVisitor extends VoidVisitorAdapter<List<Violation>> {

        private final Path filePath;

        public IfStmtVisitor(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public void visit(IfStmt n, List<Violation> collector) {
            super.visit(n, collector);
            Optional<Statement> elseStatementOptional = n.getElseStmt();
            if (elseStatementOptional.isPresent()) {
                Statement elseStatement = elseStatementOptional.get();
                int line = elseStatement.getRange()
                        .map(r -> r.begin.line)
                        .orElse(-1);
                collector.add(new Violation(filePath, line, RULE_ID, MESSAGE));
            }
        }
    }
}