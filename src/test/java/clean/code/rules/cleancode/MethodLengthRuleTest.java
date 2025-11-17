package clean.code.rules.cleancode;

import static org.assertj.core.api.Assertions.assertThat;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MethodLengthRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");

    @Test
    @DisplayName("허용된 최대 길이를 초과하는 메서드를 감지한다.")
    void check_detectsMethodLongerThanMax() {
        Rule methodLengthRule = new MethodLengthRule(10);
        String code = """
                    class Test {
                        void longMethod() { // line 2
                            System.out.println("1");
                            System.out.println("2");
                            System.out.println("3");
                            System.out.println("4");
                            System.out.println("5");
                            System.out.println("6");
                            System.out.println("7");
                            System.out.println("8");
                            System.out.println("9");
                        } // line 12
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = methodLengthRule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(2);
        assertThat(v.ruleId()).isEqualTo("MethodLength");
        assertThat(v.message()).contains("메서드 길이가 11라인입니다. (허용 기준: 10라인)");
    }

    @Test
    @DisplayName("허용된 최대 길이 이내의 메서드는 통과시킨다.")
    void check_passesMethodWithinLimit() {
        // Given
        Rule methodLengthRule = new MethodLengthRule(15);
        String code = """
                    class Test {
                        void shortMethod() {
                            if (true) {
                                System.out.println("ok");
                            }
                        }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = methodLengthRule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}