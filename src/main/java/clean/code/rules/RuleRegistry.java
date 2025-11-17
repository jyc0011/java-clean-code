package clean.code.rules;

import clean.code.config.AppRuleConfig;
import clean.code.config.RuleConfig;
import clean.code.rules.cleancode.IndentDepthRule;
import clean.code.rules.cleancode.InstanceVarCountRule;
import clean.code.rules.cleancode.LawOfDemeterRule;
import clean.code.rules.cleancode.MethodLengthRule;
import clean.code.rules.cleancode.MethodParameterRule;
import clean.code.rules.cleancode.NoElseRule;
import clean.code.rules.cleancode.NoHardcodingRule;
import clean.code.rules.oop.FirstCollectionRule;
import clean.code.rules.oop.NoDataClassRule;
import clean.code.rules.oop.WrapPrimitiveRule;
import clean.code.rules.style.ImportOrderRule;
import clean.code.rules.style.ModifierOrderRule;
import clean.code.rules.style.NamingConventionRule;
import clean.code.rules.style.NoFinalizerRule;
import clean.code.rules.style.NoWildcardImportRule;
import clean.code.rules.style.OverloadGroupingRule;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RuleRegistry {

    // --- 하드코딩된 기본값 (JSON 설정이 없을 때 사용) ---
    private static final int DEFAULT_METHOD_LENGTH_LIMIT = 15;
    private static final int DEFAULT_INSTANCE_VAR_COUNT = 2;
    private static final int DEFAULT_METHOD_PARAM_COUNT = 3;
    private static final int DEFAULT_DOT_COUNT = 1;
    private static final int DEFAULT_INDENT_DEPTH = 2;
    private static final int DEFAULT_INDENT_SIZE = 4;
    private static final int DEFAULT_PRIMITIVE_WRAP_COUNT = 2;

    private final AppRuleConfig config;

    public RuleRegistry(AppRuleConfig config) {
        this.config = config;
    }

    public List<Rule> getActiveRules() {
        List<Rule> activeRules = new ArrayList<>();
        addRuleIfEnabled(activeRules, "IndentDepth",      (c) -> new IndentDepthRule(c.max(DEFAULT_INDENT_DEPTH), DEFAULT_INDENT_SIZE, Severity.HIGH)); // 🔴
        addRuleIfEnabled(activeRules, "InstanceVarCount", (c) -> new InstanceVarCountRule(c.max(DEFAULT_INSTANCE_VAR_COUNT), Severity.MEDIUM)); // 🟠
        addRuleIfEnabled(activeRules, "MethodLength",     (c) -> new MethodLengthRule(c.max(DEFAULT_METHOD_LENGTH_LIMIT), Severity.HIGH)); // 🔴
        addRuleIfEnabled(activeRules, "MethodParameter",  (c) -> new MethodParameterRule(c.max(DEFAULT_METHOD_PARAM_COUNT), Severity.HIGH)); // 🔴
        addRuleIfEnabled(activeRules, "NoElse",           () -> new NoElseRule(Severity.MEDIUM)); // 🟠
        addRuleIfEnabled(activeRules, "LawOfDemeter",     (c) -> new LawOfDemeterRule(c.max(DEFAULT_DOT_COUNT), Severity.MEDIUM)); // 🟠
        addRuleIfEnabled(activeRules, "NoHardcoding",     () -> new NoHardcodingRule(Severity.MEDIUM)); // 🟠
        addRuleIfEnabled(activeRules, "NamingConvention", () -> new NamingConventionRule(Severity.HIGH)); // 🔴
        addRuleIfEnabled(activeRules, "NoWildcardImport", () -> new NoWildcardImportRule(Severity.HIGH)); // 🔴
        addRuleIfEnabled(activeRules, "ImportOrder",      () -> new ImportOrderRule(Severity.MEDIUM)); // 🟠
        addRuleIfEnabled(activeRules, "ModifierOrder",    () -> new ModifierOrderRule(Severity.MEDIUM)); // 🟠
        addRuleIfEnabled(activeRules, "NoFinalizer",      () -> new NoFinalizerRule(Severity.HIGH)); // 🔴
        addRuleIfEnabled(activeRules, "OverloadGrouping", () -> new OverloadGroupingRule(Severity.MEDIUM)); // 🟠
        addRuleIfEnabled(activeRules, "NoDataClass",     () -> new NoDataClassRule(Severity.HIGH)); // 🔴
        addRuleIfEnabled(activeRules, "WrapPrimitive",   (c) -> new WrapPrimitiveRule(c.max(DEFAULT_PRIMITIVE_WRAP_COUNT), Severity.MEDIUM)); // 🟠
        addRuleIfEnabled(activeRules, "FirstCollection", () -> new FirstCollectionRule(Severity.MEDIUM)); // 🟠

        return activeRules;
    }

    // --- 헬퍼 메서드 ---

    // 임계값이 없는 규칙 (on/off만)
    private void addRuleIfEnabled(List<Rule> list, String ruleId, java.util.function.Supplier<Rule> supplier) {
        if (config.getRuleConfig(ruleId).isEnabled()) {
            list.add(supplier.get());
        }
    }

    private void addRuleIfEnabled(List<Rule> list, String ruleId,
                                  java.util.function.Function<ConfigWrapper, Rule> function) {
        RuleConfig ruleConfig = config.getRuleConfig(ruleId);
        if (ruleConfig.isEnabled()) {
            list.add(function.apply(new ConfigWrapper(ruleConfig)));
        }
    }

    private record ConfigWrapper(RuleConfig config) {
        int max(int defaultValue) {
            return Optional.ofNullable(config.max()).orElse(defaultValue);
        }
    }
}