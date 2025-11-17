package clean.code.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import clean.code.report.ConsoleReporter;
import clean.code.report.Violation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CodeCheckRunnerTest {
    private final Path TEST_PATH = Paths.get("fake/path");
    private final List<Path> FAKE_FILES = List.of(TEST_PATH.resolve("File.java"));
    private final List<Violation> FAKE_VIOLATIONS = List.of(
            new Violation(FAKE_FILES.getFirst(), 10, "TestRule", "Test violation")
    );
    @Mock
    private ProjectScanner mockScanner;
    @Mock
    private Analyzer mockAnalyzer;
    @Mock
    private ConsoleReporter mockReporter;
    @InjectMocks
    private CodeCheckRunner runner;

    @Test
    @DisplayName("run 메서드는 스캔, 분석, 리포트의 흐름을 올바른 순서로 호출한다.")
    void run_shouldExecuteScanAnalyzeReportInOrder() {
        when(mockScanner.scan(TEST_PATH)).thenReturn(FAKE_FILES);
        when(mockAnalyzer.analyze(FAKE_FILES)).thenReturn(FAKE_VIOLATIONS);
        runner.run(TEST_PATH);
        verify(mockScanner, times(1)).scan(TEST_PATH);
        verify(mockAnalyzer, times(1)).analyze(FAKE_FILES);
        verify(mockReporter, times(1)).report(FAKE_VIOLATIONS);
    }

    @Test
    @DisplayName("파일이 발견되지 않으면(empty list), analyze와 report는 빈 리스트로 호출된다.")
    void run_shouldCallAnalyzeAndReportWithEmptyListsWhenNoFilesFound() {
        List<Path> emptyFileList = List.of();
        List<Violation> emptyViolationList = List.of();
        when(mockScanner.scan(any(Path.class))).thenReturn(emptyFileList);
        when(mockAnalyzer.analyze(emptyFileList)).thenReturn(emptyViolationList);
        runner.run(TEST_PATH);
        verify(mockScanner, times(1)).scan(TEST_PATH);
        verify(mockAnalyzer, times(1)).analyze(emptyFileList);
        verify(mockReporter, times(1)).report(emptyViolationList);
    }
}