package clean.code.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class ConfigLoader {

    private final ObjectMapper objectMapper;

    public ConfigLoader() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 지정된 경로의 JSON 설정 파일을 로드합니다. 파일이 없으면 빈 설정을 반환합니다.
     */
    public AppRuleConfig load(Path configPath) {
        if (configPath == null || !Files.exists(configPath)) {
            System.out.println("[INFO] Config file not found. Using default settings (all rules disabled).");
            return new AppRuleConfig(Collections.emptyMap());
        }
        try {
            return objectMapper.readValue(configPath.toFile(), AppRuleConfig.class);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to parse config file: " + configPath);
            return new AppRuleConfig(Collections.emptyMap());
        }
    }
}