package clean.code.rules.oop;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import clean.code.rules.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * [OOP] 일급 컬렉션 (컬렉션을 포함한 클래스에서 다른 멤버 변수 있는지 확인)
 */
public class FirstCollectionRule implements Rule {

    private static final String RULE_ID = "FirstCollection";
    private static final String MESSAGE = "일급 컬렉션(Collection)을 포함한 클래스는 다른 멤버 변수를 가질 수 없습니다.";
    private static final Set<String> COLLECTION_TYPES = Set.of(
            "Collection", "List", "Set", "Queue", "Deque", "Map"
    );
    private final Severity severity;

    public FirstCollectionRule(Severity severity) {
        this.severity = severity;
    }

    @Override
    public Severity getSeverity() {
        return this.severity;
    }

    @Override
    public List<Violation> check(Path filePath, CompilationUnit ast) {
        List<Violation> violations = new ArrayList<>();
        ast.accept(new FirstCollectionVisitor(filePath, severity), violations);
        return violations;
    }

    private static class FirstCollectionVisitor extends VoidVisitorAdapter<List<Violation>> {
        private final Path filePath;
        private final Severity severity;

        public FirstCollectionVisitor(Path filePath, Severity severity) {
            this.filePath = filePath;
            this.severity = severity;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<Violation> collector) {
            super.visit(n, collector);
            if (n.isInterface() || n.isRecordDeclaration() || n.isEnumDeclaration()) {
                return;
            }

            List<FieldDeclaration> instanceFields = new ArrayList<>();
            boolean hasCollection = false;

            for (FieldDeclaration field : n.getFields()) {
                if (field.isStatic() && field.isFinal()) {
                    continue;
                }
                instanceFields.add(field);
                if (isCollectionType(field)) {
                    hasCollection = true;
                }
            }

            if (hasCollection && instanceFields.size() > 1) {
                collector.add(new Violation(filePath, n.getRange().map(r -> r.begin.line).orElse(1), RULE_ID, MESSAGE,
                        severity));
            }
        }

        private boolean isCollectionType(FieldDeclaration field) {
            return field.getElementType().isClassOrInterfaceType() &&
                   COLLECTION_TYPES.contains(((ClassOrInterfaceType) field.getElementType()).getNameAsString());
        }
    }
}