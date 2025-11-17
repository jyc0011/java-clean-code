package clean.code.rules.style;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import clean.code.rules.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class NoWildcardImportRule implements Rule {

    private static final String RULE_ID = "NoWildcardImport";
    private static final String MESSAGE = "와일드카드 임포트(%s)는 금지됩니다. (Google Style Guide 3.3.1)";
    private final Severity severity;

    public NoWildcardImportRule(Severity severity) {
        this.severity = severity;
    }

    @Override
    public Severity getSeverity() {
        return this.severity;
    }

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        return ast.getImports().stream()
                .filter(ImportDeclaration::isAsterisk)
                .map(imp -> createViolation(filePath, imp))
                .collect(Collectors.toList());
    }

    private Violation createViolation(Path filePath, ImportDeclaration imp) {
        int line = imp.getRange().map(r -> r.begin.line).orElse(1);
        String staticPart = imp.isStatic() ? "static " : "";
        String importName = String.format(
                "import %s%s.*;",
                staticPart,
                imp.getNameAsString()
        );
        return new Violation(filePath, line, RULE_ID, String.format(MESSAGE, importName), severity);
    }
}