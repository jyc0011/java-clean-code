package clean.code.config;

import clean.code.core.Analyzer;
import clean.code.core.CodeCheckRunner;
import clean.code.core.ProjectScanner;
import clean.code.report.ConsoleReporter;
import clean.code.rules.Rule;
import clean.code.rules.RuleRegistry;
import java.nio.file.Path;
import java.util.List;

/**
 * 애플리케이션의 의존성(객체)을 생성하고 주입
 */
public class AppConfig {

    private final AppRuleConfig appRuleConfig;

    public AppConfig(Path configPath) {
        ConfigLoader configLoader = new ConfigLoader();
        this.appRuleConfig = configLoader.load(configPath);
    }

    public RuleRegistry ruleRegistry() {
        return new RuleRegistry(this.appRuleConfig);
    }

    public ProjectScanner projectScanner() {
        return new ProjectScanner();
    }

    public Analyzer analyzer() {
        List<Rule> activeRules = ruleRegistry().getActiveRules();
        return new Analyzer(activeRules);
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