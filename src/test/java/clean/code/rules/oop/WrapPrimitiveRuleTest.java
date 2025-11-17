package clean.code.rules.oop;

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

class WrapPrimitiveRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");

    private final Rule rule = new WrapPrimitiveRule(2);

    @Test
    @DisplayName("메서드 인자에 원시값(int, String)이 2개 이상이면 감지한다.")
    void check_detectsPrimitivesInMethodParams() {
        String code = """
                    class Test {
                        // int, String 2개
                        public void updateInfo(int age, String name) {}
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).line()).isEqualTo(3);
        assertThat(violations.get(0).ruleId()).isEqualTo("WrapPrimitive");
        assertThat(violations.get(0).message()).contains("2개 있습니다.");
    }

    @Test
    @DisplayName("생성자 인자에 원시값이 2개 이상이면 감지한다.")
    void check_detectsPrimitivesInConstructorParams() {
        String code = """
                    class Test {
                        // long, String 2개
                        public Test(long id, String email) {} // line 2
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("원시값이 1개이거나, Wrapper/Object를 사용하면 통과시킨다.")
    void check_passesSinglePrimitiveOrWrapper() {
        String code = """
                    class Test {
                        public void updateAge(int age) {} // OK (1개)
                        public void updateName(String name) {} // OK (1개)
                        public void updatePoint(Long points) {} // OK (Wrapper Class)
                        public void check(Object order) {} // OK (Object)
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}