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

class IndentDepthRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new IndentDepthRule(2, 4, Severity.HIGH);

    @Test
    @DisplayName("Indent Depth 3단계(if-for-while)를 감지한다.")
    void check_detectsDepth3() {
        String code = """
                    class Test {
                        void method(int x) {
                            if (x > 0) {
                                for (int i = 0; i < x; i++) {
                                    while (x > i) {
                                        System.out.println(i);
                                    }
                                }
                            }
                        }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation violation = violations.getFirst();
        assertThat(violation.line()).isEqualTo(5);
        assertThat(violation.message()).contains("들여쓰기 깊이가 3입니다. (허용 기준: 2)");
        assertThat(violation.severity()).isEqualTo(Severity.HIGH);
    }

    @Test
    @DisplayName("Indent Depth 2단계는 통과시킨다.")
    void check_passesDepth2() {
        String code = """
                    class Test {
                        void method(int x) {
                            if (x > 0) {
                                for (int i = 0; i < x; i++) {
                                    System.out.println(i);
                                }
                            }
                        }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}