package clean.code.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ProjectScannerTest {

    @TempDir
    Path tempDir; // 테스트용 임시 디렉토리

    ProjectScanner projectScanner;

    @BeforeEach
    void setUp() throws IOException {
        projectScanner = new ProjectScanner();

        // Given (테스트용 가상 파일 구조 생성)
        // 1. 루트 Java 파일
        Files.createFile(tempDir.resolve("RootClass.java"));
        Files.createFile(tempDir.resolve("README.md")); // 무시되어야 함

        // 2. 하위 디렉토리
        Path subDir = tempDir.resolve("service");
        Files.createDirectory(subDir);
        Files.createFile(subDir.resolve("UserService.java"));
        Files.createFile(subDir.resolve("config.txt")); // 무시되어야 함

        // 3. 더 깊은 하위 디렉토리
        Path deepDir = subDir.resolve("impl");
        Files.createDirectory(deepDir);
        Files.createFile(deepDir.resolve("UserServiceImpl.java"));
    }

    @Test
    @DisplayName("지정된 디렉토리 하위의 모든 .java 파일만 재귀적으로 찾아낸다.")
    void scan_findsAllJavaFilesRecursively() throws IOException {
        // When
        List<Path> javaFiles = projectScanner.scan(tempDir);

        // Then
        // .java 파일 3개만 찾아야 하고, .md나 .txt 파일은 무시해야 합니다.
        assertThat(javaFiles)
                .hasSize(3)
                .extracting(Path::getFileName) // 파일 이름만 추출
                .containsExactlyInAnyOrder(
                        Paths.get("RootClass.java"),
                        Paths.get("UserService.java"),
                        Paths.get("UserServiceImpl.java")
                );
    }

    @Test
    @DisplayName("존재하지 않는 경로를 스캔하면 빈 리스트를 반환한다.")
    void scan_returnsEmptyListForNonExistentPath() throws IOException {
        // Given
        Path nonExistentPath = tempDir.resolve("non-existent-dir");

        // When
        List<Path> javaFiles = projectScanner.scan(nonExistentPath);

        // Then
        assertThat(javaFiles).isEmpty();
    }
}