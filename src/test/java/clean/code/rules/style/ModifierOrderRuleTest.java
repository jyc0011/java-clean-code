package clean.code.rules.style;

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

class ModifierOrderRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new ModifierOrderRule(Severity.MEDIUM);

    @Test
    @DisplayName("필드 선언의 제어자 순서가 틀린 경우(static public) 감지한다.")
    void check_detectsIncorrectFieldModifierOrder() {
        String code = """
            class Test {
                static public final int MAX_COUNT = 10;
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(2);
        assertThat(v.ruleId()).isEqualTo("ModifierOrder");
        assertThat(v.message()).contains("static public final")
                .contains("public static final");
        assertThat(v.severity()).isEqualTo(Severity.MEDIUM);
    }

    @Test
    @DisplayName("메서드 선언의 제어자 순서가 틀린 경우(static public) 감지한다.")
    void check_detectsIncorrectMethodModifierOrder() {
        String code = """
            class Test {
                static public void main(String[] args) {}
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().line()).isEqualTo(2);
        assertThat(violations.getFirst().severity()).isEqualTo(Severity.MEDIUM);
    }

    @Test
    @DisplayName("올바른 제어자 순서(public static final)는 통과시킨다.")
    void check_passesCorrectModifierOrder() {
        String code = """
            class Test {
                public static final int MAX_COUNT = 10;
                protected abstract void doSomething();
                private static String name;
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}