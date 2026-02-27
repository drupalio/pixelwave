package pixelweave.render.command;

import java.util.ArrayList;
import java.util.List;

public final class DisplayList {
    private final List<DrawCommand> commands = new ArrayList<>();

    public void add(DrawCommand command) {
        commands.add(command);
    }

    public void clear() {
        commands.clear();
    }

    public List<DrawCommand> commands() {
        return List.copyOf(commands);
    }
}
