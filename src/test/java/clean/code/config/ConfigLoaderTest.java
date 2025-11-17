package clean.code.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigLoaderTest {

    private ConfigLoader configLoader;

    @BeforeEach
    void setUp() {
        configLoader = new ConfigLoader();
    }

    @Test
    @DisplayName("test-config.json 파일을 로드하여 AppRuleConfig 객체로 변환한다.")
    void load_parsesJsonFileCorrectly() throws URISyntaxException, IOException {
        Path configPath = Paths.get(getClass().getClassLoader().getResource("test-config.json").toURI());
        AppRuleConfig config = configLoader.load(configPath);
        assertThat(config).isNotNull();
        RuleConfig noElse = config.getRuleConfig("NoElse");
        assertThat(noElse.isEnabled()).isTrue();
        assertThat(noElse.max()).isNull();
        RuleConfig methodLength = config.getRuleConfig("MethodLength");
        assertThat(methodLength.isEnabled()).isTrue();
        assertThat(methodLength.max()).isEqualTo(20);
        assertThat(config.getRuleConfig("NamingConvention").isEnabled()).isFalse();
        assertThat(config.getRuleConfig("NonExistentRule").isEnabled()).isFalse();
    }
}