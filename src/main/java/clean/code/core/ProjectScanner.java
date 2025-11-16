package clean.code.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectScanner {

    private static final String JAVA_EXTENSION = ".java";

    /**
     * 지정된 경로와 그 하위 디렉토리에서 모든 .java 파일을 재귀적으로 찾음
     *
     * @param projectPath 검사를 시작할 루트 디렉토리
     * @return .java 파일 경로 리스트
     */
    public List<Path> scan(Path projectPath) {
        if (!Files.exists(projectPath) || !Files.isDirectory(projectPath)) {
            return Collections.emptyList();
        }

        try (Stream<Path> walk = Files.walk(projectPath)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(JAVA_EXTENSION))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to scan project directory: " + projectPath);
            return Collections.emptyList();
        }
    }
}