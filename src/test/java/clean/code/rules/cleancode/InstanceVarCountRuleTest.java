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

class InstanceVarCountRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new InstanceVarCountRule(2); // 최대 2개 허용

    @Test
    @DisplayName("인스턴스 변수가 3개인 클래스를 감지한다.")
    void check_detectsClassWith3InstanceVars() {
        // Given
        String code = """
                    public class Test { // line 1
                        private int a;
                        private String b;
                        private Object c;
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);

        // When
        List<Violation> violations = rule.check(TEST_FILE, ast);

        // Then
        assertThat(violations).hasSize(1);
        Violation v = violations.get(0);
        assertThat(v.line()).isEqualTo(1);
        assertThat(v.ruleId()).isEqualTo("InstanceVarCount");
        assertThat(v.message()).contains("인스턴스 변수가 3개입니다. (허용 기준: 2개)");
    }

    @Test
    @DisplayName("static 변수(클래스 변수)는 개수에 포함하지 않는다.")
    void check_ignoresStaticFields() {
        // Given
        String code = """
                    public class Test {
                        private int a; // 1
                        private String b; // 2
                        private static final int MAX_COUNT = 10; // static
                        private static Object instance; // static
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);

        // When
        List<Violation> violations = rule.check(TEST_FILE, ast);

        // Then
        assertThat(violations).isEmpty(); // 2개이므로 통과
    }

    @Test
    @DisplayName("한 줄에 여러 변수가 선언된 경우(int a, b;) 모두 카운트한다.")
    void check_countsMultiVariableDeclarations() {
        // Given
        String code = """
                    public class Test { // line 1
                        private int a, b; // 2
                        private String c; // 1
                    }
                """; // 총 3개
        CompilationUnit ast = StaticJavaParser.parse(code);

        // When
        List<Violation> violations = rule.check(TEST_FILE, ast);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).line()).isEqualTo(1);
    }
}