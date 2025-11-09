package clean.code;

import clean.code.config.AppConfig;
import clean.code.core.CodeCheckRunner;
import picocli.CommandLine;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "code-checker",
        mixinStandardHelpOptions = true,
        version = "code-checker 0.1",
        description = "Java Clean Code & Google Style Guide Checker"
)
public class Application implements Callable<Integer> {

    @CommandLine.Parameters(
            index = "0",
            description = "검사할 Java 프로젝트의 소스 경로"
    )
    Path projectPath;

    /**
     * Picocli가 실행하는 메인 로직
     */
    @Override
    public Integer call() throws Exception {
        AppConfig appConfig = new AppConfig();
        CodeCheckRunner runner = appConfig.codeCheckRunner();
        runner.run(projectPath);
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }
}