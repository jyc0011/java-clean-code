package clean.code.report;

import static org.assertj.core.api.Assertions.assertThat;

import clean.code.rules.Severity;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConsoleReporterTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private ConsoleReporter consoleReporter;

    @BeforeEach
    void setUp() {
        consoleReporter = new ConsoleReporter();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("위반 사항이 없으면, '[SUCCESS] No violations found!'를 출력한다.")
    void report_printsSuccessMessageWhenEmpty() {
        List<Violation> emptyList = List.of();
        consoleReporter.report(emptyList);
        String output = getConsoleOutput();
        assertThat(output).isEqualTo("[SUCCESS] No violations found!");
    }

    @Test
    @DisplayName("위반 사항이 있으면, 파일별로 그룹핑하고 요약(X violations in Y files)을 출력한다.")
    void report_groupsViolationsByFileAndPrintsSummary() {
        Path fileA = Paths.get("src/main/Order.java");
        Path fileB = Paths.get("src/main/Member.java");

        List<Violation> violations = List.of(
                new Violation(fileA, 10, "MethodLength", "메서드 길이 15 초과", Severity.HIGH),
                new Violation(fileB, 20, "NoElse", "else 사용", Severity.MEDIUM),
                new Violation(fileA, 45, "NoHardcoding", "하드코딩된 문자열", Severity.MEDIUM)
        );

        consoleReporter.report(violations);
        String output = getConsoleOutput();
        String expectedOutput = """
                [FAIL] Found 3 violations in 2 files!

                🟠 Member.java:20 [NoElse]
                   - else 사용

                🔴 Order.java:10 [MethodLength]
                   - 메서드 길이 15 초과
                🟠 Order.java:45 [NoHardcoding]
                   - 하드코딩된 문자열""";

        assertThat(output).isEqualTo(expectedOutput.replace("\r\n", "\n"));
    }

    /**
     * outContent에 저장된 콘솔 출력 내용을 문자열로 반환합니다.
     */
    private String getConsoleOutput() {
        return outContent.toString().trim().replace("\r\n", "\n"); // OS 호환
    }
}