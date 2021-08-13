package net.oskarstrom.dashloader.api.serializer;

import java.io.IOException;

public interface DashSerializer<O> {
	O deserialize(String path) throws IOException;

	void serialize(String path, O object) throws IOException;
}
