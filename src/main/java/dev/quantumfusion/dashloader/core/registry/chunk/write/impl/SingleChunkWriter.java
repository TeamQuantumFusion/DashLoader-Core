package dev.quantumfusion.dashloader.core.registry.chunk.write.impl;

import dev.quantumfusion.dashloader.core.DashObjectMetadata;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.WriteFailCallback;
import dev.quantumfusion.dashloader.core.registry.chunk.write.AbstractSingleChunkWriter;
import dev.quantumfusion.dashloader.core.util.DashThreading;

import java.util.List;

public class SingleChunkWriter<R, D extends Dashable<R>> extends AbstractSingleChunkWriter<D, R, D> {
	protected SingleChunkWriter(byte pos, DashRegistryWriter writer, WriteFailCallback<R, D> callback, Class<D> dashType, Class<R> targetClass) {
		super(pos, writer, callback, dashType, targetClass);
	}

	public static <R, D extends Dashable<R>> SingleChunkWriter<R, D> create(
			byte pos, DashRegistryWriter writer, WriteFailCallback<R, D> callback, DashObjectMetadata<R, D> metadata) {

		return new SingleChunkWriter<>(pos, writer, callback, metadata.dashClass, metadata.targetClass);
	}

	@Override
	public D computeListItem(R object) {
		return constructor.create(object, writer);
	}

	@Override
	public D[] writeOut(List<D> list, String taskName) {
		return DashThreading.runWriteTask(taskName, list);
	}
}
