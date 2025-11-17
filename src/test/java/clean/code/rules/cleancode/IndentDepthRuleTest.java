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

class IndentDepthRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new IndentDepthRule(2, 4);

    @Test
    @DisplayName("Indent Depth 3단계(if-for-while)를 감지한다.")
    void check_detectsDepth3() {
        String code = """
                    class Test {
                        void method(int x) { // Base indent (col 5)
                            if (x > 0) { // Depth 1 (col 9)
                                for (int i = 0; i < x; i++) { // Depth 2 (col 13)
                                    while (x > i) { // Depth 3 (col 17) - VIOLATION
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
        assertThat(violations.get(0).line()).isEqualTo(5); // while문
        assertThat(violations.get(0).message()).contains("들여쓰기 깊이가 3입니다. (허용 기준: 2)");
    }

    @Test
    @DisplayName("Indent Depth 2단계는 통과시킨다.")
    void check_passesDepth2() {
        String code = """
                    class Test {
                        void method(int x) {
                            if (x > 0) { // Depth 1
                                for (int i = 0; i < x; i++) { // Depth 2
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