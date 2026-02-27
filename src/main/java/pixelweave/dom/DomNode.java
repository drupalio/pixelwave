package pixelweave.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DomNode {
    private final String tagName;
    private final Map<String, String> attributes;
    private final List<DomNode> children;
    private String text;

    public DomNode(String tagName) {
        this(tagName, new HashMap<>(), new ArrayList<>(), "");
    }

    public DomNode(String tagName, Map<String, String> attributes, List<DomNode> children, String text) {
        this.tagName = tagName;
        this.attributes = attributes;
        this.children = children;
        this.text = text;
    }

    public String tagName() {
        return tagName;
    }

    public Map<String, String> attributes() {
        return attributes;
    }

    public List<DomNode> children() {
        return children;
    }

    public String text() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addChild(DomNode child) {
        children.add(child);
    }

    public String attribute(String name) {
        return attributes.get(name);
    }
}
