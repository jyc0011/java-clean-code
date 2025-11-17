package clean.code.rules.style;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [Style] 제어자(Modifier) 표준 순서 강제 (Google Style Guide: 4.8.7 Modifiers)
 */
public class ModifierOrderRule implements Rule {

    private static final String RULE_ID = "ModifierOrder";

    private static final List<Modifier.Keyword> STANDARD_ORDER = List.of(
            Modifier.Keyword.PUBLIC,
            Modifier.Keyword.PROTECTED,
            Modifier.Keyword.PRIVATE,
            Modifier.Keyword.ABSTRACT,
            Modifier.Keyword.DEFAULT,
            Modifier.Keyword.STATIC,
            Modifier.Keyword.FINAL,
            Modifier.Keyword.SEALED,
            Modifier.Keyword.NON_SEALED,
            Modifier.Keyword.TRANSIENT,
            Modifier.Keyword.VOLATILE,
            Modifier.Keyword.SYNCHRONIZED,
            Modifier.Keyword.NATIVE,
            Modifier.Keyword.STRICTFP
    );

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new ModifierOrderVisitor(filePath), violations);
        return violations;
    }

    private static class ModifierOrderVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;

        public ModifierOrderVisitor(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            checkModifiers(n, n.getModifiers(), collector);
        }

        @Override
        public void visit(MethodDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            checkModifiers(n, n.getModifiers(), collector);
        }

        @Override
        public void visit(FieldDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            checkModifiers(n, n.getModifiers(), collector);
        }

        @Override
        public void visit(ConstructorDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            checkModifiers(n, n.getModifiers(), collector);
        }

        private void checkModifiers(Node node, NodeList<Modifier> actualModifiers, List<Violation> collector) {
            if (actualModifiers.isEmpty()) {
                return;
            }
            List<Modifier.Keyword> actualKeywords = actualModifiers.stream()
                    .map(Modifier::getKeyword)
                    .toList();
            List<Modifier.Keyword> expectedKeywords = new ArrayList<>(actualKeywords);
            expectedKeywords.sort(Comparator.comparingInt(STANDARD_ORDER::indexOf)
            );
            if (!actualKeywords.equals(expectedKeywords)) {
                int line = node.getRange().map(r -> r.begin.line).orElse(1);

                String actual = actualKeywords.stream().map(Enum::name).collect(Collectors.joining(" ")).toLowerCase();
                String expected = expectedKeywords.stream().map(Enum::name).collect(Collectors.joining(" "))
                        .toLowerCase();

                String message = String.format(
                        "제어자 순서가 표준(%s)과 다릅니다. 실제 순서: [%s], 권장 순서: [%s]",
                        "Google Style Guide 4.8.7", actual, expected
                );
                collector.add(new Violation(filePath, line, RULE_ID, message));
            }
        }
    }
}