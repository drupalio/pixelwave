package pixelweave.dom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public final class HtmlLoader {
    public DomNode parse(Path path) throws IOException {
        String html = Files.readString(path);
        return parseString(html);
    }

    public DomNode parseString(String html) {
        Document document = Jsoup.parse(html);
        Element root = document.children().first();
        if (root == null) {
            throw new IllegalArgumentException("No root element found");
        }
        return convert(root);
    }

    private DomNode convert(Element element) {
        DomNode node = new DomNode(element.tagName(), new HashMap<>(), new java.util.ArrayList<>(), "");
        element.attributes().forEach(attr -> node.attributes().put(attr.getKey(), attr.getValue()));

        for (Node child : element.childNodes()) {
            if (child instanceof Element childElement) {
                node.addChild(convert(childElement));
            } else if (child instanceof TextNode textNode) {
                String text = textNode.text().trim();
                if (!text.isEmpty()) {
                    node.setText(text);
                }
            }
        }

        return node;
    }
}
