package clean.code;

import clean.code.config.AppConfig;
import clean.code.core.CodeCheckRunner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import picocli.CommandLine;

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

    @CommandLine.Option(
            names = {"-c", "--config"},
            description = "규칙 설정 JSON 파일 경로 (기본값: 실행 위치의 checker-config.json)"
    )
    Path configPath;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Picocli가 실행하는 메인 로직
     */
    @Override
    public Integer call() throws Exception {
        if (configPath == null) {
            configPath = Paths.get(System.getProperty("user.dir"), "checker-config.json");
        }
        AppConfig appConfig = new AppConfig(configPath);
        CodeCheckRunner runner = appConfig.codeCheckRunner();
        runner.run(projectPath);
        return 0;
    }
}