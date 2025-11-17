package clean.code.rules.style;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * [Style] 명명 규칙 검사 (Google Style Guide: 5. Naming)
 */
public class NamingConventionRule implements Rule {

    private static final String RULE_ID = "NamingConvention";
    private static final Pattern UPPER_CAMEL_CASE = Pattern.compile("^[A-Z][A-Za-z0-9]*$");
    private static final Pattern LOWER_CAMEL_CASE = Pattern.compile("^[a-z][A-Za-z0-9]*$");
    private static final Pattern UPPER_SNAKE_CASE = Pattern.compile("^[A-Z0-9_]+$");

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new NamingVisitor(filePath), violations);
        return violations;
    }

    private static class NamingVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;

        public NamingVisitor(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            String name = n.getNameAsString();
            if (!UPPER_CAMEL_CASE.matcher(name).matches()) {
                addViolation(n.getName().getRange().map(r -> r.begin.line).orElse(1),
                        name, "클래스/인터페이스", "UpperCamelCase (ex: MyClass)", collector);
            }
        }

        @Override
        public void visit(MethodDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            String name = n.getNameAsString();
            if (!LOWER_CAMEL_CASE.matcher(name).matches()) {
                addViolation(n.getName().getRange().map(r -> r.begin.line).orElse(1),
                        name, "메서드", "lowerCamelCase (ex: myMethod)", collector);
            }
        }

        @Override
        public void visit(FieldDeclaration n, List<Violation> collector) {
            super.visit(n, collector);

            boolean isConstant = n.isStatic() && n.isFinal();

            for (VariableDeclarator v : n.getVariables()) {
                String name = v.getNameAsString();
                int line = v.getName().getRange().map(r -> r.begin.line).orElse(1);

                if (isConstant) {
                    if (!UPPER_SNAKE_CASE.matcher(name).matches()) {
                        addViolation(line, name, "상수(static final)", "UPPER_SNAKE_CASE (ex: MY_CONSTANT)", collector);
                    }
                } else {
                    if (!LOWER_CAMEL_CASE.matcher(name).matches()) {
                        addViolation(line, name, "필드", "lowerCamelCase (ex: myField)", collector);
                    }
                }
            }
        }

        private void addViolation(int line, String name, String type, String expected, List<Violation> collector) {
            String message = String.format(
                    "%s 이름 '%s'가 명명 규칙(%s)을 따르지 않습니다.",
                    type, name, expected
            );
            collector.add(new Violation(filePath, line, RULE_ID, message));
        }
    }
}