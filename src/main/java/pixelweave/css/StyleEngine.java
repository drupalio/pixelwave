package pixelweave.css;

import pixelweave.dom.DomNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StyleEngine {
    public Map<DomNode, ComputedStyle> computeStyles(DomNode root, List<StyleRule> rules) {
        Map<DomNode, ComputedStyle> styles = new HashMap<>();
        applyRecursive(root, rules, styles);
        return styles;
    }

    private void applyRecursive(DomNode node, List<StyleRule> rules, Map<DomNode, ComputedStyle> styles) {
        ComputedStyle style = new ComputedStyle();

        for (StyleRule rule : rules) {
            if (matches(node, rule.selector())) {
                rule.declarations().forEach(style::set);
            }
        }

        styles.put(node, style);
        for (DomNode child : node.children()) {
            applyRecursive(child, rules, styles);
        }
    }

    private boolean matches(DomNode node, String selector) {
        if (selector.startsWith("#")) {
            return selector.substring(1).equals(node.attribute("id"));
        }

        if (selector.startsWith(".")) {
            String className = node.attribute("class");
            return className != null && List.of(className.split("\\s+")).contains(selector.substring(1));
        }

        return selector.equalsIgnoreCase(node.tagName());
    }
}
