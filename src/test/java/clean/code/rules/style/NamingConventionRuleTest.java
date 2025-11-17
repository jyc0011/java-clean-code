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

class NamingConventionRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new NamingConventionRule(Severity.HIGH);

    @Test
    @DisplayName("클래스 이름이 UpperCamelCase가 아닌 경우(myClass) 감지한다.")
    void check_detectsNonUpperCamelCaseClass() {
        String code = "class myClass {}";
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(1);
        assertThat(v.message()).contains("UpperCamelCase");
        assertThat(v.severity()).isEqualTo(Severity.HIGH);
    }

    @Test
    @DisplayName("메서드 이름이 lowerCamelCase가 아닌 경우(MyMethod) 감지한다.")
    void check_detectsNonLowerCamelCaseMethod() {
        String code = "class Test { void MyMethod() {} }";
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(1);
        assertThat(v.message()).contains("lowerCamelCase");
        assertThat(v.severity()).isEqualTo(Severity.HIGH);
    }

    @Test
    @DisplayName("상수(static final) 이름이 UPPER_SNAKE_CASE가 아닌 경우(my_Const) 감지한다.")
    void check_detectsNonUpperSnakeCaseConstant() {
        String code = "class Test { static final int my_Const = 1; }";
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(1);
        assertThat(v.message()).contains("UPPER_SNAKE_CASE");
        assertThat(v.severity()).isEqualTo(Severity.HIGH);
    }

    @Test
    @DisplayName("일반 필드 이름이 lowerCamelCase가 아닌 경우(MyField) 감지한다.")
    void check_detectsNonLowerCamelCaseField() {
        String code = "class Test { int MyField; }";
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(1);
        assertThat(v.message()).contains("lowerCamelCase");
        assertThat(v.severity()).isEqualTo(Severity.HIGH);
    }

    @Test
    @DisplayName("모든 명명 규칙을 준수한 경우 통과시킨다.")
    void check_passesCorrectNames() {
        String code = """
            package com.example.good;
            
            class GoodClassName {
                private static final int MY_CONSTANT = 10;
                private String myField;
                
                void myMethodName() {}
            }
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}