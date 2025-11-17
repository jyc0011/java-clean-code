package clean.code.rules.style;

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

class NoFinalizerRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new NoFinalizerRule(Severity.HIGH);

    @Test
    @DisplayName("Object.finalize() 메서드 오버라이드를 감지한다.")
    void check_detectsFinalizeOverride() {
        String code = """
            class Test {
                @Override
                protected void finalize() throws Throwable {
                    super.finalize();
                }
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(3);
        assertThat(v.ruleId()).isEqualTo("NoFinalizer");
        assertThat(v.severity()).isEqualTo(Severity.HIGH);
    }

    @Test
    @DisplayName("이름만 같고 파라미터가 다른 'finalize' 메서드는 무시한다.")
    void check_ignoresOverloadedFinalize() {
        String code = """
            class Test {
                public void finalize(boolean force) { 
                }
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}