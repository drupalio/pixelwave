package pixelweave.render;

public record Color(float r, float g, float b, float a) {
    public static Color fromHex(String hex) {
        String clean = hex.startsWith("#") ? hex.substring(1) : hex;
        if (clean.length() != 6 && clean.length() != 8) {
            throw new IllegalArgumentException("Hex color must be 6 or 8 chars: " + hex);
        }

        int rgb = (int) Long.parseLong(clean, 16);
        if (clean.length() == 6) {
            float r = ((rgb >> 16) & 0xFF) / 255f;
            float g = ((rgb >> 8) & 0xFF) / 255f;
            float b = (rgb & 0xFF) / 255f;
            return new Color(r, g, b, 1f);
        }

        float r = ((rgb >> 24) & 0xFF) / 255f;
        float g = ((rgb >> 16) & 0xFF) / 255f;
        float b = ((rgb >> 8) & 0xFF) / 255f;
        float a = (rgb & 0xFF) / 255f;
        return new Color(r, g, b, a);
    }

    public static Color named(String value) {
        return switch (value.toLowerCase()) {
            case "white" -> new Color(1f, 1f, 1f, 1f);
            case "black" -> new Color(0f, 0f, 0f, 1f);
            default -> throw new IllegalArgumentException("Unsupported color name: " + value);
        };
    }
}
