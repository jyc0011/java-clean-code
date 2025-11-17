package clean.code.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProjectScannerTest {

    @TempDir
    Path tempDir;

    ProjectScanner projectScanner;

    @BeforeEach
    void setUp() throws IOException {
        projectScanner = new ProjectScanner();
        Files.createFile(tempDir.resolve("RootClass.java"));
        Files.createFile(tempDir.resolve("README.md"));
        Path subDir = tempDir.resolve("service");
        Files.createDirectory(subDir);
        Files.createFile(subDir.resolve("UserService.java"));
        Files.createFile(subDir.resolve("config.txt"));
        Path deepDir = subDir.resolve("impl");
        Files.createDirectory(deepDir);
        Files.createFile(deepDir.resolve("UserServiceImpl.java"));
    }

    @Test
    @DisplayName("지정된 디렉토리 하위의 모든 .java 파일만 재귀적으로 찾아낸다.")
    void scan_findsAllJavaFilesRecursively() throws IOException {
        List<Path> javaFiles = projectScanner.scan(tempDir);
        assertThat(javaFiles)
                .hasSize(3)
                .extracting(Path::getFileName)
                .containsExactlyInAnyOrder(
                        Paths.get("RootClass.java"),
                        Paths.get("UserService.java"),
                        Paths.get("UserServiceImpl.java")
                );
    }

    @Test
    @DisplayName("존재하지 않는 경로를 스캔하면 빈 리스트를 반환한다.")
    void scan_returnsEmptyListForNonExistentPath() throws IOException {
        Path nonExistentPath = tempDir.resolve("non-existent-dir");
        List<Path> javaFiles = projectScanner.scan(nonExistentPath);
        assertThat(javaFiles).isEmpty();
    }
}