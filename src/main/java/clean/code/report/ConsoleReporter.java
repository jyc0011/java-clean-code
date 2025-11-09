package clean.code.report;

import java.util.List;

public class ConsoleReporter {
    public void report(List<Violation> violations) {
        if (violations.isEmpty()) {
            System.out.println("[SUCCESS] No violations found!");
            return;
        }

        System.out.println("[FAIL] Found " + violations.size() + " violations!");
        // TODO: TDD 5단계 - 위반 사항 상세 출력 로직 구현
        for (Violation v : violations) {
            System.out.printf("🔴 %s:%d [%s]%n   - %s%n",
                    v.filePath().getFileName(), v.line(), v.ruleId(), v.message());
        }
    }
}