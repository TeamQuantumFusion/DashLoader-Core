package dev.quantumfusion.dashloader.core.registry.chunk.write.impl;

import dev.quantumfusion.dashloader.core.DashObjectMetadata;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.WriteFailCallback;
import dev.quantumfusion.dashloader.core.registry.chunk.write.AbstractSingleChunkWriter;
import dev.quantumfusion.dashloader.core.util.DashThreading;

import java.util.List;

public class FloatingSingleChunkWriter<R, D extends Dashable<R>> extends AbstractSingleChunkWriter<R, R, D> {
	public FloatingSingleChunkWriter(byte pos, DashRegistryWriter writer, WriteFailCallback<R, D> callback, Class<D> dashType, Class<R> targetClass) {
		super(pos, writer, callback, dashType, targetClass);
	}

	public static <R, D extends Dashable<R>> FloatingSingleChunkWriter<R, D> create(
			byte pos, DashRegistryWriter writer, WriteFailCallback<R, D> callback, DashObjectMetadata<R, D> metadata) {

		return new FloatingSingleChunkWriter<>(pos, writer, callback, metadata.dashClass, metadata.targetClass);
	}

	@Override
	public R computeListItem(R object) {
		return object;
	}

	@Override
	public D[] writeOut(List<R> list, String taskName) {
		return DashThreading.runFloatingSingleWriteTask(taskName, list, constructor, writer);
	}


}
