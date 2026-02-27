package pixelweave.css;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CssParser {
    public List<StyleRule> parse(String css) {
        List<StyleRule> rules = new ArrayList<>();
        String[] blocks = css.split("}");

        for (String block : blocks) {
            String trimmed = block.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String[] parts = trimmed.split("\\{", 2);
            if (parts.length != 2) {
                continue;
            }

            String selector = parts[0].trim();
            Map<String, String> declarations = parseDeclarations(parts[1]);
            rules.add(new StyleRule(selector, declarations));
        }

        return rules;
    }

    private Map<String, String> parseDeclarations(String section) {
        Map<String, String> declarations = new HashMap<>();
        String[] lines = section.split(";");

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String[] property = trimmed.split(":", 2);
            if (property.length != 2) {
                continue;
            }

            declarations.put(property[0].trim(), property[1].trim());
        }

        return declarations;
    }
}
