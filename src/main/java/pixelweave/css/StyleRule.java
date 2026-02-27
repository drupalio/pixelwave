package pixelweave.css;

import java.util.Map;

public record StyleRule(String selector, Map<String, String> declarations) {
}
