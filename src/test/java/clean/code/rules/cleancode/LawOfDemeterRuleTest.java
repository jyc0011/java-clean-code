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

class LawOfDemeterRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new LawOfDemeterRule(1);

    @Test
    @DisplayName("점이 2개인 메서드 호출을 감지한다 (a.b.c())")
    void check_detectsTwoDotsMethodCall() {
        String code = """
                    class Test {
                        void method(A a) {
                            a.b.c(); // line 3
                        }
                        static class A { B b; }
                        static class B { void c() {} }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).line()).isEqualTo(3);
        assertThat(violations.get(0).ruleId()).isEqualTo("LawOfDemeter");
    }

    @Test
    @DisplayName("점이 2개인 필드 접근을 감지한다 (this.a.b)")
    void check_detectsTwoDotsFieldAccess() {
        String code = """
                    class Test {
                        A a;
                        void method() {
                            String s = this.a.b; // line 4
                        }
                        static class A { String b; }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).line()).isEqualTo(4);
    }

    @Test
    @DisplayName("점이 1개인 호출은 통과시킨다 (a.b())")
    void check_passesOneDotCall() {
        String code = """
                    class Test {
                        void method(A a) {
                            a.b(); // OK
                            this.field = 1; // OK
                            "String".length(); // OK
                        }
                        int field;
                        static class A { void b() {} }
                    }
                """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}