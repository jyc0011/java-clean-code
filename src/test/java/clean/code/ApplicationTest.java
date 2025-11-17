package clean.code;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;


class ApplicationTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    @TempDir
    Path tempDir;

    @Test
    @DisplayName("CLI 인자로 올바른 프로젝트 경로를 전달하면, projectPath 필드에 해당 경로가 저장된다.")
    void cli_parsesProjectPathCorrectly() {
        Application app = new Application();
        String[] args = {"/path/to/my/target/project"};
        new CommandLine(app).parseArgs(args);
        assertThat(app.projectPath).isEqualTo(Paths.get("/path/to/my/target/project"));
    }

    @Test
    @DisplayName("CLI 인자로 경로를 전달하지 않으면, Picocli가 사용법 에러를 반환한다.")
    void cli_returnsErrorWhenPathIsMissing() {
        Application app = new Application();
        String[] args = {};
        int exitCode = new CommandLine(app).execute(args);
        assertThat(exitCode).isEqualTo(CommandLine.ExitCode.USAGE);
    }

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String getConsoleOutput() {
        return outContent.toString().trim().replace("\r\n", "\n");
    }

    @Test
    @DisplayName("애플리케이션을 실행하면, 위반 규칙(NoElse, MethodLength 등)을 감지하고 콘솔에 리포트한다.")
    void run_endToEnd_detectsViolationsCorrectly() throws IOException {
        String badCode = """
                    package com.example;
                    
                    class BadCode {
                        void method(int x) {
                            if (x > 0) {} else {}
                        }
                    }
                """;
        Path badCodePath = tempDir.resolve("BadCode.java");
        Files.writeString(badCodePath, badCode);

        String longMethodCode = """
                    package com.example;
                    
                    class LongMethod {
                        void veryLongMethod() {
                            System.out.println(1);
                            System.out.println(2);
                            System.out.println(3);
                            System.out.println(4);
                            System.out.println(5);
                            System.out.println(6);
                            System.out.println(7);
                            System.out.println(8);
                            System.out.println(9);
                            System.out.println(10);
                            System.out.println(11);
                            System.out.println(12);
                            System.out.println(13);
                            System.out.println(14);
                            System.out.println(15);
                        }
                    }
                """;
        Path longMethodPath = tempDir.resolve("LongMethod.java");
        Files.writeString(longMethodPath, longMethodCode);
        String goodCode = "class GoodCode {}";
        Files.writeString(tempDir.resolve("GoodCode.java"), goodCode);
        Files.writeString(tempDir.resolve("README.md"), "# Readme");
        String[] args = {tempDir.toAbsolutePath().toString()};
        Application app = new Application();
        int exitCode = new CommandLine(app).execute(args);
        String consoleOutput = getConsoleOutput();
        assertThat(exitCode).isEqualTo(0);
        assertThat(consoleOutput).contains("[FAIL] Found 31 violations in 2 files!");
        assertThat(consoleOutput)
                .contains("BadCode.java:5 [NoElse]");
        assertThat(consoleOutput)
                .contains("LongMethod.java:4 [MethodLength]")
                .contains("메서드 길이가 17라인입니다. (허용 기준: 15라인)");
        assertThat(consoleOutput).contains("LongMethod.java:6 [NoHardcoding]");
        assertThat(consoleOutput).contains("LongMethod.java:5 [LawOfDemeter]");
    }
}