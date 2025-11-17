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

class OverloadGroupingRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new OverloadGroupingRule();

    @Test
    @DisplayName("오버로드된 메서드(doSomething) 사이에 필드가 끼어있는 경우 감지한다.")
    void check_detectsFieldSeparatingOverloads() {
        String code = """
                    class Test {
                        public void doSomething(String s) {} // line 2
                        
                        private int counter; // line 4
                        
                        public void doSomething(String s, int i) {} // line 6
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).line()).isEqualTo(6); // 두 번째 doSomething
        assertThat(violations.get(0).message()).contains("'doSomething'이(가) 이전에 나타났지만");
    }

    @Test
    @DisplayName("오버로드된 메서드 사이에 다른 메서드가 끼어있는 경우 감지한다.")
    void check_detectsMethodSeparatingOverloads() {
        String code = """
                    class Test {
                        public void doSomething(String s) {} // line 2
                        
                        public void otherMethod() {} // line 4
                        
                        public void doSomething(String s, int i) {} // line 6
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).line()).isEqualTo(6);
    }

    @Test
    @DisplayName("오버로드된 생성자(Constructor) 사이에 멤버가 끼어있는 경우 감지한다.")
    void check_detectsMemberSeparatingOverloadedConstructors() {
        String code = """
                    class Test {
                        public Test() {} // line 2
                        
                        private int counter; // line 4
                        
                        public Test(String s) {} // line 6
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).line()).isEqualTo(6);
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