package clean.code.report;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ConsoleReporter {
    public void report(List<Violation> violations) {
        if (violations.isEmpty()) {
            System.out.println("[SUCCESS] No violations found!");
            return;
        }
        Map<Path, List<Violation>> groupedViolations = violations.stream()
                .collect(Collectors.groupingBy(
                        Violation::filePath,
                        TreeMap::new,
                        Collectors.toList()
                ));
        int totalViolations = violations.size();
        int filesWithViolations = groupedViolations.size();
        System.out.printf("[FAIL] Found %d violations in %d files!%n%n",
                totalViolations, filesWithViolations);
        groupedViolations.forEach((path, violationList) -> {
            for (Violation v : violationList) {
                System.out.printf("🔴 %s:%d [%s]%n",
                        path.getFileName(), v.line(), v.ruleId());
                System.out.printf("   - %s%n", v.message());
            }
            System.out.println();
        });
    }
}