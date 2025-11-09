package clean.code.config;

import clean.code.core.Analyzer;
import clean.code.core.CodeCheckRunner;
import clean.code.core.ProjectScanner;
import clean.code.report.ConsoleReporter;

/**
 * 애플리케이션의 의존성(객체)을 생성하고 주입
 */
public class AppConfig {
    public ProjectScanner projectScanner() {
        return new ProjectScanner();
    }

    public Analyzer analyzer() {
        // TODO: 규칙(Rule) 리스트를 생성하여 Analyzer에 주입
        return new Analyzer();
    }

    public ConsoleReporter consoleReporter() {
        return new ConsoleReporter();
    }

    public CodeCheckRunner codeCheckRunner() {
        return new CodeCheckRunner(
                projectScanner(),
                analyzer(),
                consoleReporter()
        );
    }
}