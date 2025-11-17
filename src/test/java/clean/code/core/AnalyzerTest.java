package clean.code.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import clean.code.report.Violation;
import clean.code.rules.Rule;

import com.github.javaparser.ast.CompilationUnit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyzerTest {

    @TempDir
    Path tempDir;
    @Mock
    private Rule mockRule1;
    @Mock
    private Rule mockRule2;

    @Test
    @DisplayName("Analyzer는 .java 파일을 파싱하여 모든 Rule에게 전달하고 결과를 취합한다.")
    void analyze_parsesFileAndDelegatesToRules() throws IOException {
        String javaCode = "class Test { }";
        Path javaFile = tempDir.resolve("Test.java");
        Files.writeString(javaFile, javaCode);
        Violation violation1 = new Violation(javaFile, 1, "Rule1", "Error 1");
        Violation violation2 = new Violation(javaFile, 2, "Rule2", "Error 2");
        when(mockRule1.check(any(Path.class), any(CompilationUnit.class))).thenReturn(List.of(violation1));
        when(mockRule2.check(any(Path.class), any(CompilationUnit.class))).thenReturn(List.of(violation2));
        Analyzer analyzer = new Analyzer(List.of(mockRule1, mockRule2));
        List<Violation> results = analyzer.analyze(List.of(javaFile));
        assertThat(results)
                .hasSize(2)
                .containsExactlyInAnyOrder(violation1, violation2);
    }
}