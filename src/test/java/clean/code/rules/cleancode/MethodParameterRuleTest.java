package clean.code.rules.cleancode;

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

class MethodParameterRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new MethodParameterRule(3, Severity.HIGH);

    @Test
    @DisplayName("인자가 4개인 메서드를 감지한다.")
    void check_detectsMethodWith4Params() {
        String code = """
                    class Test {
                        void tooMany(int a, int b, String c, Object d) {
                        }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(2);
        assertThat(v.message()).contains("4개입니다. (허용 기준: 3개)");
        assertThat(v.severity()).isEqualTo(Severity.HIGH);
    }

    @Test
    @DisplayName("인자가 4개인 생성자를 감지한다.")
    void check_detectsConstructorWith4Params() {
        String code = """
                    class Test {
                        Test(int a, int b, String c, Object d) {
                        }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().line()).isEqualTo(2);
        assertThat(violations.getFirst().severity()).isEqualTo(Severity.HIGH);
    }

    @Test
    @DisplayName("인자가 3개 이하인 메서드/생성자는 통과시킨다.")
    void check_passesMethodsWith3OrLessParams() {
        String code = """
                    class Test {
                        Test(int a, int b, String c) { }
                        void ok(int a, int b) { }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}