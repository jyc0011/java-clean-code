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

class WrapPrimitiveRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new WrapPrimitiveRule(2, Severity.MEDIUM);

    @Test
    @DisplayName("메서드 인자에 원시값(int, String)이 2개 이상이면 감지한다.")
    void check_detectsPrimitivesInMethodParams() {
        String code = """
            class Test {
                public void updateInfo(int age, String name) {}
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(2);
        assertThat(v.ruleId()).isEqualTo("WrapPrimitive");
        assertThat(v.message()).contains("원시값(primitive/String)이 2개 있습니다.");
        assertThat(v.severity()).isEqualTo(Severity.MEDIUM);
    }

    @Test
    @DisplayName("생성자 인자에 원시값이 2개 이상이면 감지한다.")
    void check_detectsPrimitivesInConstructorParams() {
        String code = """
            class Test {
                public Test(long id, String email) {}
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().severity()).isEqualTo(Severity.MEDIUM);
    }

    @Test
    @DisplayName("원시값이 1개이거나, Wrapper/Object를 사용하면 통과시킨다.")
    void check_passesSinglePrimitiveOrWrapper() {
        String code = """
            class Test {
                public void updateAge(int age) {}
                public void updateName(String name) {}
                public void updatePoint(Long points) {}
                public void check(Object order) {}
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}