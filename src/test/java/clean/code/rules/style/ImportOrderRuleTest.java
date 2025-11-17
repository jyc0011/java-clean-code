package clean.code.rules.style;

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

class ImportOrderRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new ImportOrderRule();

    @Test
    @DisplayName("non-static 임포트가 static 임포트보다 먼저 오는 경우 감지한다.")
    void check_detectsNonStaticBeforeStatic() {
        String code = """
                    import java.util.List; // line 1 (non-static)
                    import static org.junit.jupiter.api.Assertions.fail; // line 2 (static)
                    
                    class Test {}
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().line()).isEqualTo(2); // static 임포트가 잘못된 위치
        assertThat(violations.getFirst().ruleId()).isEqualTo("ImportOrder");
        assertThat(violations.getFirst().message()).contains("static 임포트는 non-static 임포트보다 먼저 와야 합니다.");
    }

    @Test
    @DisplayName("non-static 그룹 내부의 ASCII 순서가 틀린 경우 감지한다.")
    void check_detectsIncorrectAsciiOrderInNonStatic() {
        String code = """
                    import java.util.Map; // line 1
                    import java.util.List; // line 2 (Map보다 앞에 와야 함)
                    
                    class Test {}
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().line()).isEqualTo(2);
        assertThat(violations.getFirst().message()).contains("임포트 순서가 ASCII 정렬과 다릅니다.");
    }

    @Test
    @DisplayName("static 그룹 내부의 ASCII 순서가 틀린 경우 감지한다.")
    void check_detectsIncorrectAsciiOrderInStatic() {
        String code = """
                    import static org.junit.jupiter.api.Assertions.fail; // line 1
                    import static org.assertj.core.api.Assertions.assertThat; // line 2 (junit보다 앞에 와야 함)
                    
                    class Test {}
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().line()).isEqualTo(2);
    }

    @Test
    @DisplayName("올바른 임포트 순서(static -> non-static, ASCII)는 통과시킨다.")
    void check_passesCorrectOrder() {
        String code = """
                    import static org.assertj.core.api.Assertions.assertThat;
                    import static org.junit.jupiter.api.Assertions.fail;
                    
                    import java.util.List;
                    import java.util.Map;
                    
                    class Test {}
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}