package clean.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest {

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
}