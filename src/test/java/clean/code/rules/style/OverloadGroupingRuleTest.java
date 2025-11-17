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

class OverloadGroupingRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new OverloadGroupingRule(Severity.MEDIUM);

    @Test
    @DisplayName("오버로드된 메서드(doSomething) 사이에 필드가 끼어있는 경우 감지한다.")
    void check_detectsFieldSeparatingOverloads() {
        String code = """
                    class Test {
                        public void doSomething(String s) {}
                        
                        private int counter;
                        
                        public void doSomething(String s, int i) {}
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(6);
        assertThat(v.message()).contains("'doSomething'이(가) 이전에 나타났지만");
        assertThat(v.severity()).isEqualTo(Severity.MEDIUM);
    }

    @Test
    @DisplayName("오버로드된 메서드 사이에 다른 메서드가 끼어있는 경우 감지한다.")
    void check_detectsMethodSeparatingOverloads() {
        String code = """
                    class Test {
                        public void doSomething(String s) {}
                        
                        public void otherMethod() {}
                        
                        public void doSomething(String s, int i) {}
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().line()).isEqualTo(6);
        assertThat(violations.getFirst().severity()).isEqualTo(Severity.MEDIUM);
    }

    @Test
    @DisplayName("오버로드된 생성자(Constructor) 사이에 멤버가 끼어있는 경우 감지한다.")
    void check_detectsMemberSeparatingOverloadedConstructors() {
        String code = """
                    class Test {
                        public Test() {}
                        
                        private int counter;
                        
                        public Test(String s) {}
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().line()).isEqualTo(6);
        assertThat(violations.getFirst().severity()).isEqualTo(Severity.MEDIUM);
    }

    @Test
    @DisplayName("오버로드된 메서드/생성자가 잘 그룹화된 경우 통과시킨다.")
    void check_passesCorrectlyGroupedOverloads() {
        String code = """
                    class Test {
                        public Test() {}
                        public Test(String s) {}
                        
                        private int counter;
                        
                        public void doSomething(String s) {}
                        public void doSomething(String s, int i) {}
                        
                        public void otherMethod() {}
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}