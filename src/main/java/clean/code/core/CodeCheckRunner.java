package clean.code.core;

import clean.code.report.ConsoleReporter;
import clean.code.report.Violation;

import java.nio.file.Path;
import java.util.List;

/**
 * 실제 스캔, 분석, 리포팅의 흐름(workflow)을 담당하는 실행 객체
 */
public class CodeCheckRunner {
    private final ProjectScanner projectScanner;
    private final Analyzer analyzer;
    private final ConsoleReporter consoleReporter;

    public CodeCheckRunner(ProjectScanner projectScanner, Analyzer analyzer, ConsoleReporter consoleReporter) {
        this.projectScanner = projectScanner;
        this.analyzer = analyzer;
        this.consoleReporter = consoleReporter;
    }

    /**
     * 메인 실행 로직
     *
     * @param projectPath 검사할 프로젝트 경로
     */
    public void run(Path projectPath) {
        System.out.println("[INFO] Scanning project: " + projectPath.toAbsolutePath());
        List<Path> javaFiles = projectScanner.scan(projectPath);
        List<Violation> violations = analyzer.analyze(javaFiles);
        consoleReporter.report(violations);
        System.out.println("[INFO] Scan finished.");
    }
}