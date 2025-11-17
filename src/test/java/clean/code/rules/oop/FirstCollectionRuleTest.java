package clean.code.rules.oop;

import static org.assertj.core.api.Assertions.assertThat;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import clean.code.rules.Severity;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FirstCollectionRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new FirstCollectionRule(Severity.MEDIUM);

    @Test
    @DisplayName("컬렉션(List)과 다른 멤버 변수(int)를 함께 가진 클래스를 감지한다.")
    void check_detectsCollectionWithOtherField() {
        String code = """
            import java.util.List;
            
            public class Numbers {
                private List<Integer> numbers;
                private int total;
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(3);
        assertThat(v.ruleId()).isEqualTo("FirstCollection");
        assertThat(v.severity()).isEqualTo(Severity.MEDIUM);
    }

    @Test
    @DisplayName("컬렉션(Map)과 다른 멤버 변수를 함께 가진 클래스를 감지한다.")
    void check_detectsCollectionWithOtherFieldMap() {
        String code = """
            import java.util.Map;
            
            public class Users {
                private Map<Long, User> users;
                private String adminName;
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().severity()).isEqualTo(Severity.MEDIUM);
    }

    @Test
    @DisplayName("컬렉션만 가진 클래스(일급 컬렉션)는 통과시킨다.")
    void check_passesFirstClassCollection() {
        String code = """
            import java.util.List;
            
            public class Numbers {
                private List<Integer> numbers;
                private static final int DEFAULT_CAPACITY = 10;
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("컬렉션이 없는 클래스는 통과시킨다.")
    void check_passesClassWithoutCollection() {
        String code = """
            public class User {
                private String name;
                private int age;
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}