package clean.code.rules.oop;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * [OOP] Getter/Setter만 있는 데이터 클래스(DTO 제외) 감지
 */
public class NoDataClassRule implements Rule {

    private static final String RULE_ID = "NoDataClass";
    private static final String MESSAGE = "클래스가 데이터와 Getter/Setter만 가지고 있습니다. " +
            "핵심 도메인 객체라면, 객체의 상태를 변경하는 '의도'가 드러나는 메서드를 추가하세요.";

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new DataClassVisitor(filePath), violations);
        return violations;
    }

    private static class DataClassVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;

        public DataClassVisitor(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<Violation> collector) {
            super.visit(n, collector);

            if (n.isInterface() || n.isRecordDeclaration() || n.isEnumDeclaration() ||
                    n.getNameAsString().endsWith("DTO")) {
                return;
            }
            boolean hasFields = !n.getFields().isEmpty();
            boolean hasOnlyGettersSettersConstructors = true;
            for (BodyDeclaration<?> member : n.getMembers()) {
                if (member.isFieldDeclaration() || member.isConstructorDeclaration()
                        || member.isInitializerDeclaration()) {
                    continue;
                }

                if (member instanceof MethodDeclaration md) {
                    if (!isGetter(md) && !isSetter(md)) {
                        hasOnlyGettersSettersConstructors = false;
                        break;
                    }
                }
            }

            if (hasFields && hasOnlyGettersSettersConstructors) {
                collector.add(new Violation(filePath, n.getRange().map(r -> r.begin.line).orElse(1), RULE_ID, MESSAGE));
            }
        }

        private boolean isGetter(MethodDeclaration md) {
            String name = md.getNameAsString();
            if (!md.getParameters().isEmpty() || md.getType().isVoidType()) {
                return false;
            }
            if (name.startsWith("get") && name.length() > 3 && Character.isUpperCase(name.charAt(3))) {
                return true;
            }
            String typeName = md.getType().asString();
            return name.startsWith("is") && name.length() > 2 && Character.isUpperCase(name.charAt(2)) &&
                    (typeName.equals("boolean") || typeName.equals("Boolean"));
        }

        private boolean isSetter(MethodDeclaration md) {
            String name = md.getNameAsString();
            if (md.getParameters().size() != 1 || !md.getType().isVoidType()) {
                return false;
            }
            return name.startsWith("set") && name.length() > 3 && Character.isUpperCase(name.charAt(3));
        }
    }
}