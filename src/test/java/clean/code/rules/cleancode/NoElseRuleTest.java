package clean.code.rules.cleancode;

import static org.assertj.core.api.Assertions.assertThat;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NoElseRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private Rule noElseRule;

    @BeforeEach
    void setUp() {
        noElseRule = new NoElseRule();
    }

    @Test
    @DisplayName("'else' 키워드가 포함된 코드는 위반을 감지해야 한다.")
    void check_detectsElseStatement() {
        String code = """
                    class Test {
                        void method(int x) { // line 2
                            if (x > 0) {
                                System.out.println("Positive");
                            } else { // line 5
                                System.out.println("Not positive");
                            }
                        }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = noElseRule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation violation = violations.getFirst();
        assertThat(violation.line()).isEqualTo(5); // 'else' 키워드의 라인
        assertThat(violation.ruleId()).isEqualTo("NoElse");
    }

    @Test
    @DisplayName("'else if' 구문도 위반으로 감지해야 한다.")
    void check_detectsElseIfStatement() {
        String code = """
                    class Test {
                        void method(int x) {
                            if (x > 0) {
                                // ...
                            } else if (x < 0) { // line 5
                                // ...
                            }
                        }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = noElseRule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().line()).isEqualTo(5);
    }

    @Test
    @DisplayName("else가 없는 'if' 구문은 위반이 아니다.")
    void check_ignoresIfWithoutElse() {
        String code = """
                    class Test {
                        void method(int x) {
                            // Guard clause
                            if (x <= 0) { 
                                return;
                            }
                            System.out.println("Positive");
                        }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = noElseRule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}