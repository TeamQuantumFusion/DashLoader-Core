package dev.quantumfusion.dashloader.core.api;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.WriteFailCallback;
import dev.quantumfusion.dashloader.core.registry.chunk.write.AbstractChunkWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Creator<R, D extends Dashable<R>> {
	private final WriteFailCallback<R, D> failCallback;
	@Nullable
	private final DashConstructor<R, D> constructor;
	private final AbstractChunkWriter<?, ?> chunkWriter;

	public Creator(WriteFailCallback<R, D> failCallback, @Nullable DashConstructor<R, D> constructor, AbstractChunkWriter<?, ?> chunkWriter) {
		this.failCallback = failCallback;
		this.constructor = constructor;
		this.chunkWriter = chunkWriter;
	}

	public Creator(WriteFailCallback<R, D> failCallback, AbstractChunkWriter<?, ?> chunkWriter) {
		this.failCallback = failCallback;
		this.constructor = null;
		this.chunkWriter = chunkWriter;
	}

	public D create(R raw, DashRegistryWriter writer) {
		if (constructor == null) throw new RuntimeException("Callback only Creator");
		if (raw == null) {
			return fallback(null, writer, new NullPointerException());
		}

		try {
			return constructor.invoke(raw, writer);
		} catch (Throwable throwable) {
			return fallback(raw, writer, throwable);
		}
	}

	@NotNull
	public D fallback(R obj, DashRegistryWriter writer, Throwable throwable) {
		final D fail = failCallback.fail(obj, writer);
		if (fail != null) return fail;
		else
			throw new RuntimeException("Could not create " + chunkWriter.dashType.getSimpleName() + " in a " + chunkWriter.getClass().getSimpleName(), throwable);
	}
}
