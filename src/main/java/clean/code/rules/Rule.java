package clean.code.rules;

import clean.code.report.Violation;
import com.github.javaparser.ast.CompilationUnit;
import java.nio.file.Path;
import java.util.List;

/**
 * 모든 검사 규칙이 구현해야 하는 인터페이스
 */
public interface Rule {

    /**
     * 주어진 Java AST(추상 구문 트리)를 분석하여 위반 사항을 찾습니다.
     *
     * @param filePath 현재 검사 중인 파일 경로 (Violation 생성 시 필요)
     * @param ast      파싱이 완료된 Java AST
     * @return 이 규칙에 의해 발견된 위반 사항 리스트
     */
    List<Violation> check(Path filePath, CompilationUnit ast);
}