package pixelweave.render;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class ShapeRenderer {
    private int programId;
    private int vaoId;
    private int vboId;
    private int colorUniform;

    public void init() {
        String vertexShaderSource = """
                #version 330 core
                layout (location = 0) in vec2 aPos;
                void main() {
                    gl_Position = vec4(aPos, 0.0, 1.0);
                }
                """;

        String fragmentShaderSource = """
                #version 330 core
                uniform vec4 uColor;
                out vec4 FragColor;
                void main() {
                    FragColor = uColor;
                }
                """;

        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);

        programId = glCreateProgram();
        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            String message = glGetProgramInfoLog(programId);
            throw new IllegalStateException("Failed to link rectangle shader program: " + message);
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();

        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        colorUniform = glGetUniformLocation(programId, "uColor");
    }

    public void cleanup() {
        if (vboId != 0) {
            glDeleteBuffers(vboId);
            vboId = 0;
        }
        if (vaoId != 0) {
            glDeleteVertexArrays(vaoId);
            vaoId = 0;
        }
        if (programId != 0) {
            glDeleteProgram(programId);
            programId = 0;
        }
    }

    public void drawRect(int x, int y, int width, int height, Color color) {
        drawRect(x, y, width, height, color, Integer.MAX_VALUE);
    }

    public void drawRect(int x, int y, int width, int height, Color color, int viewportHeight) {
        if (width <= 0 || height <= 0 || programId == 0) {
            return;
        }

        int viewportWidth = currentViewportWidth();
        int effectiveViewportHeight = viewportHeight == Integer.MAX_VALUE ? currentViewportHeight() : viewportHeight;

        float left = toNdcX(x, viewportWidth);
        float right = toNdcX(x + width, viewportWidth);
        float top = toNdcY(y, effectiveViewportHeight);
        float bottom = toNdcY(y + height, effectiveViewportHeight);

        FloatBuffer vertices = BufferUtils.createFloatBuffer(12);
        vertices.put(left).put(top);
        vertices.put(right).put(top);
        vertices.put(right).put(bottom);
        vertices.put(left).put(top);
        vertices.put(right).put(bottom);
        vertices.put(left).put(bottom);
        vertices.flip();

        glUseProgram(programId);
        glUniform4f(colorUniform, color.r(), color.g(), color.b(), color.a());

        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        glUseProgram(0);
    }

    public void drawRoundedRect(int x, int y, int width, int height, int radius, Color color) {
        drawRoundedRect(x, y, width, height, radius, color, Integer.MAX_VALUE);
    }

    public void drawRoundedRect(int x, int y, int width, int height, int radius, Color color, int viewportHeight) {
        drawRect(x, y, width, height, color, viewportHeight);
    }

    private int compileShader(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            String message = glGetShaderInfoLog(shader);
            throw new IllegalStateException("Failed to compile shader: " + message);
        }

        return shader;
    }

    private int currentViewportWidth() {
        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport);
        return viewport[2];
    }

    private int currentViewportHeight() {
        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport);
        return viewport[3];
    }

    private float toNdcX(int x, int viewportWidth) {
        return (2.0f * x / viewportWidth) - 1.0f;
    }

    private float toNdcY(int y, int viewportHeight) {
        return 1.0f - (2.0f * y / viewportHeight);
    }
}
