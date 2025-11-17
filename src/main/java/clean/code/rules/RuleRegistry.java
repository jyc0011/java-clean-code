package clean.code.rules;

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
import java.util.List;

/**
 * 활성화된 규칙들을 생성하고 관리(등록)합니다. 향후 config 파일(미구현)을 읽어 규칙을 On/Off하거나, 임계값(예: 메서드 길이 10)을 설정하는 로직이 추가될 수 있습니다.
 */
public class RuleRegistry {
    private static final int DEFAULT_METHOD_LENGTH_LIMIT = 15;
    private static final int DEFAULT_INSTANCE_VAR_COUNT = 2;
    private static final int DEFAULT_METHOD_PARAM_COUNT = 3;
    private static final int DEFAULT_DOT_COUNT = 1;
    private static final int DEFAULT_INDENT_DEPTH = 2;
    private static final int DEFAULT_INDENT_SIZE = 4;
    private static final int DEFAULT_PRIMITIVE_WRAP_COUNT = 2;

    /**
     * 현재 활성화된 모든 규칙의 인스턴스를 반환합니다.
     */
    public List<Rule> getActiveRules() {
        return List.of(
                new NoElseRule(),
                new MethodLengthRule(DEFAULT_METHOD_LENGTH_LIMIT),
                new InstanceVarCountRule(DEFAULT_INSTANCE_VAR_COUNT),
                new MethodParameterRule(DEFAULT_METHOD_PARAM_COUNT),
                new NoHardcodingRule(),
                new LawOfDemeterRule(DEFAULT_DOT_COUNT),
                new IndentDepthRule(DEFAULT_INDENT_DEPTH, DEFAULT_INDENT_SIZE),
                new NoWildcardImportRule(),
                new NoFinalizerRule(),
                new ModifierOrderRule(),
                new ImportOrderRule(),
                new NamingConventionRule(),
                new OverloadGroupingRule(),
                new NoDataClassRule(),
                new WrapPrimitiveRule(DEFAULT_PRIMITIVE_WRAP_COUNT),
                new FirstCollectionRule()
        );
    }
}