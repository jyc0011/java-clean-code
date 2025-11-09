package clean.code.core;

import clean.code.report.ConsoleReporter;
import clean.code.report.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodeCheckRunnerTest {
    @Mock
    private ProjectScanner mockScanner;
    @Mock
    private Analyzer mockAnalyzer;
    @Mock
    private ConsoleReporter mockReporter;
    @InjectMocks
    private CodeCheckRunner runner;

    private final Path TEST_PATH = Paths.get("fake/path");
    private final List<Path> FAKE_FILES = List.of(TEST_PATH.resolve("File.java"));
    private final List<Violation> FAKE_VIOLATIONS = List.of(
            new Violation(FAKE_FILES.get(0), 10, "TestRule", "Test violation")
    );

    @BeforeEach
    void setUp() {
        when(mockScanner.scan(TEST_PATH)).thenReturn(FAKE_FILES);
        when(mockAnalyzer.analyze(FAKE_FILES)).thenReturn(FAKE_VIOLATIONS);
    }

    @Test
    @DisplayName("run 메서드는 스캔, 분석, 리포트의 흐름을 올바른 순서로 호출한다.")
    void run_shouldExecuteScanAnalyzeReportInOrder() {
        runner.run(TEST_PATH);
        verify(mockScanner, times(1)).scan(TEST_PATH);
        verify(mockAnalyzer, times(1)).analyze(FAKE_FILES);
        verify(mockReporter, times(1)).report(FAKE_VIOLATIONS);
    }

    @Test
    @DisplayName("파일이 발견되지 않으면(empty list), 분석이나 리포트는 호출되지 않는다.")
    void run_shouldNotCallAnalyzeOrReportWhenNoFilesFound() {
        when(mockScanner.scan(any(Path.class))).thenReturn(List.of());
        runner.run(TEST_PATH);
        verify(mockScanner, times(1)).scan(TEST_PATH);
        verify(mockAnalyzer, never()).analyze(any());
        verify(mockReporter, never()).report(any());
    }
}