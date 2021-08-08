package net.oskarstrom.dashloader.api.serializer;

import java.io.IOException;
import java.nio.file.Path;

public interface DashSerializer<O> {
	O deserialize(Path path) throws IOException;

	void serialize(Path path, O object) throws IOException;
}
