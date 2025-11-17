package clean.code;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
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
    void run_endToEnd_detectsViolationsCorrectly() throws IOException, URISyntaxException {
        Path testConfigPath = Paths.get(getClass().getClassLoader().getResource("test-config.json").toURI());
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
                            int a=0;
                            int b=0;
                            int c=0;
                            int d=0;
                            int e=0;
                            int f=0;
                            int g=0;
                            int h=0;
                            int i=0;
                            int j=0;
                            int k=0;
                            int l=0;
                            int m=0;
                            int n=0;
                            int o=0;
                            int p=0;
                            int q=0;
                            int r=0;
                            int s=0;
                            int t=0;
                        }
                    }
                """;
        Path longMethodPath = tempDir.resolve("LongMethod.java");
        Files.writeString(longMethodPath, longMethodCode);
        String goodCode = "class GoodCode {}";
        Files.writeString(tempDir.resolve("GoodCode.java"), goodCode);
        Files.writeString(tempDir.resolve("README.md"), "# Readme");
        String[] args = {
                tempDir.toAbsolutePath().toString(),
                "-c",
                testConfigPath.toAbsolutePath().toString()
        };
        Application app = new Application();
        int exitCode = new CommandLine(app).execute(args);
        String consoleOutput = getConsoleOutput();
        assertThat(consoleOutput).contains("[FAIL] Found 2 violations in 2 files!");
        assertThat(consoleOutput)
                .contains("BadCode.java:5 [NoElse]");
        assertThat(consoleOutput)
                .contains("LongMethod.java:4 [MethodLength]")
                .contains("메서드 길이가 22라인입니다. (허용 기준: 20라인)");
        assertThat(consoleOutput).doesNotContain("NoHardcoding");
    }
}