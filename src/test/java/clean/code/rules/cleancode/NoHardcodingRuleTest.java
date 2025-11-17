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

class NoHardcodingRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new NoHardcodingRule();

    @Test
    @DisplayName("매직 스트링(String) 리터럴을 감지한다.")
    void check_detectsMagicString() {
        String code = """
                    class Test {
                        void checkStatus(String status) {
                            if (status.equals("ACTIVE")) { // line 3
                                // ...
                            }
                        }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).line()).isEqualTo(3);
        assertThat(violations.get(0).ruleId()).isEqualTo("NoHardcoding");
        assertThat(violations.get(0).message()).contains("\"ACTIVE\"");
    }

    @Test
    @DisplayName("매직 넘버(Integer) 리터럴을 감지한다.")
    void check_detectsMagicNumber() {
        String code = """
                    class Test {
                        int getValue() {
                            return 100; // line 3 (0, 1, -1은 제외)
                        }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).line()).isEqualTo(3);
        assertThat(violations.get(0).message()).contains("100");
    }

    @Test
    @DisplayName("static final 상수 선언의 리터럴은 무시한다.")
    void check_ignoresStaticFinalConstants() {
        String code = """
                    class Test {
                        private static final String ACTIVE_STATUS = "ACTIVE"; // OK
                        private static final int MAX_COUNT = 100; // OK
                        
                        void checkStatus(String status) {
                            if (status.equals(ACTIVE_STATUS)) { // OK
                                // ...
                            }
                        }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Annotation의 인자로 사용된 리터럴은 무시한다.")
    void check_ignoresAnnotationLiterals() {
        String code = """
                    @DisplayName("TDD 테스트") // OK
                    class Test {
                        @Deprecated(since = "1.5") // OK
                        int value = 5; // Fail
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).line()).isEqualTo(4); // 5 감지
    }
}