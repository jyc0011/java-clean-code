package clean.code.rules;

/**
 * 규칙의 중요도(Severity)를 나타내는 Enum
 * (HIGH: 🔴, MEDIUM: 🟠)
 */
public enum Severity {
    HIGH("🔴"),
    MEDIUM("🟠");

    private final String icon;

    Severity(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}