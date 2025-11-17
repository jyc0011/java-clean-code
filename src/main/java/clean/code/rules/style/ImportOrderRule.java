package clean.code.rules.style;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * [Style] 임포트 순서 검사 (Google Style Guide: 3.3.3 Ordering and spacing)
 */
public class ImportOrderRule implements Rule {

    private static final String RULE_ID = "ImportOrder";

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        List<ImportDeclaration> imports = ast.getImports();

        if (imports.isEmpty()) {
            return violations;
        }

        boolean hasEncounteredNonStatic = false;
        String lastStaticImport = "";
        String lastNonStaticImport = "";

        for (ImportDeclaration imp : imports) {
            String importName = imp.getNameAsString();
            int line = imp.getRange().map(r -> r.begin.line).orElse(1);

            if (imp.isStatic()) {
                if (hasEncounteredNonStatic) {
                    violations.add(new Violation(filePath, line, RULE_ID,
                            "static 임포트는 non-static 임포트보다 먼저 와야 합니다."));
                }

                if (importName.compareTo(lastStaticImport) < 0) {
                    violations.add(createAsciiOrderViolation(filePath, line, importName, lastStaticImport));
                }
                lastStaticImport = importName;

            } else {
                hasEncounteredNonStatic = true;
                if (importName.compareTo(lastNonStaticImport) < 0) {
                    violations.add(createAsciiOrderViolation(filePath, line, importName, lastNonStaticImport));
                }
                lastNonStaticImport = importName;
            }
        }
        return violations;
    }

    private Violation createAsciiOrderViolation(Path filePath, int line, String current, String previous) {
        String message = String.format(
                "임포트 순서가 ASCII 정렬과 다릅니다. '%s'는 '%s'보다 앞에 와야 합니다.",
                current, previous
        );
        return new Violation(filePath, line, RULE_ID, message);
    }
}