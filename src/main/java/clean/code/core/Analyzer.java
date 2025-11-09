package clean.code.core;

import clean.code.report.Violation;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

// TODO: TDD 4단계 - 규칙(Rule)을 주입받도록 수정
public class Analyzer {

    // (임시) 빈 분석 메서드
    public List<Violation> analyze(List<Path> javaFiles) {
        System.out.println("[DEBUG] Analyzing " + javaFiles.size() + " files...");
        // TODO: JavaParser를 이용한 실제 분석 로직 구현
        return Collections.emptyList();
    }
}