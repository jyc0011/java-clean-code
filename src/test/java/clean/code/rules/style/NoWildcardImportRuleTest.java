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

class NoWildcardImportRuleTest {

    private final Path TEST_FILE = Paths.get("Test.java");
    private final Rule rule = new NoWildcardImportRule(Severity.HIGH);

    @Test
    @DisplayName("일반 와일드카드 임포트(import java.util.*;)를 감지한다.")
    void check_detectsNormalWildcardImport() {
        String code = """
            package com.test;
            
            import java.util.*;
            
            class Test {}
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(3);
        assertThat(v.ruleId()).isEqualTo("NoWildcardImport");
        assertThat(v.message()).contains("import java.util.*;");
        assertThat(v.severity()).isEqualTo(Severity.HIGH);
    }

    @Test
    @DisplayName("Static 와일드카드 임포트(import static ... *)를 감지한다.")
    void check_detectsStaticWildcardImport() {
        String code = """
            package com.test;
            
            import static org.assertj.core.api.Assertions.*;
            
            class Test {}
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).hasSize(1);
        Violation v = violations.getFirst();
        assertThat(v.line()).isEqualTo(3);
        assertThat(v.ruleId()).isEqualTo("NoWildcardImport");
        assertThat(v.severity()).isEqualTo(Severity.HIGH);
    }

    @Test
    @DisplayName("일반 임포트(import java.util.List;)는 통과시킨다.")
    void check_passesNormalImports() {
        String code = """
            package com.test;
            
            import java.util.List;
            import java.util.Map;
            
            class Test {}
        """;
        CompilationUnit ast = StaticJavaParser.parse(code);
        List<Violation> violations = rule.check(TEST_FILE, ast);
        assertThat(violations).isEmpty();
    }
}