package clean.code.report;

import java.nio.file.Path;

/**
 * 코드 위반 사항을 나타내는 데이터 객체 (DTO)
 */
public record Violation(Path filePath, int line, String ruleId, String message) {
}