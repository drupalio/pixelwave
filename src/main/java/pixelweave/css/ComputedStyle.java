package pixelweave.css;

import java.util.HashMap;
import java.util.Map;

public final class ComputedStyle {
    private final Map<String, String> values = new HashMap<>();

    public void set(String property, String value) {
        values.put(property, value);
    }

    public String get(String property) {
        return values.get(property);
    }

    public String getOrDefault(String property, String defaultValue) {
        return values.getOrDefault(property, defaultValue);
    }

    public Map<String, String> values() {
        return Map.copyOf(values);
    }
}
