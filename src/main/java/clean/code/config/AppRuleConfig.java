package clean.code.config;

import java.util.Map;

public record AppRuleConfig(Map<String, RuleConfig> rules) {
    public RuleConfig getRuleConfig(String ruleId) {
        return rules.getOrDefault(ruleId, new RuleConfig("off", null));
    }
}