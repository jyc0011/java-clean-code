package clean.code.core;

import clean.code.report.Violation;
import clean.code.rules.Rule;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Analyzer {
    private final List<Rule> rules;

    public Analyzer(List<Rule> rules) {
        this.rules = rules;
    }

    /**
     * Java 파일 목록을 분석하여 위반 사항을 반환합니다.
     */
    public List<Violation> analyze(List<Path> javaFiles) {
        List<Violation> allViolations = new ArrayList<>();

        for (Path javaFile : javaFiles) {
            Optional<CompilationUnit> astOptional = parseFile(javaFile);
            if (astOptional.isEmpty()) {
                continue;
            }

            CompilationUnit ast = astOptional.get();

            for (Rule rule : this.rules) {
                allViolations.addAll(rule.check(javaFile, ast));
            }
        }

        System.out.println("[DEBUG] Analyzing " + javaFiles.size() + " files...");
        return allViolations;
    }

    /**
     * 파일을 파싱하여 AST(CompilationUnit)를 반환합니다. [클린코드] 한 가지 일만 담당 (파일 파싱)
     */
    private Optional<CompilationUnit> parseFile(Path javaFile) {
        try {
            return Optional.of(StaticJavaParser.parse(javaFile));
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to parse file: " + javaFile);
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to analyze (parse error): " + javaFile);
            return Optional.empty();
        }
    }
}