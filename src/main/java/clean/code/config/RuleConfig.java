package clean.code.config;

public record RuleConfig(String status, Integer max) {

    public boolean isEnabled() {
        return "on".equalsIgnoreCase(status);
    }
}