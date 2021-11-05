package dev.quantumfusion.dashloader.core.registry.chunk.write.impl;

import dev.quantumfusion.dashloader.core.DashObjectMetadata;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.Creator;
import dev.quantumfusion.dashloader.core.api.DashConstructor;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.WriteFailCallback;
import dev.quantumfusion.dashloader.core.registry.chunk.write.AbstractChunkWriter;
import dev.quantumfusion.dashloader.core.util.DashThreading;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FloatingMultiChunkWriter<R, D extends Dashable<R>> extends AbstractChunkWriter<R, D> {
	private final Object2ObjectMap<Class<R>, Creator<R, D>> constructorMap = new Object2ObjectOpenHashMap<>();
	private final List<Class<?>> dashClasses = new ArrayList<>();
	private final List<Class<?>> rawClasses = new ArrayList<>();
	private final List<R> rawList = new ArrayList<>();

	protected FloatingMultiChunkWriter(byte pos, DashRegistryWriter writer, WriteFailCallback<R, D> callback, Class<?> dashType) {
		super(pos, writer, callback, dashType);
	}


	public static <R, D extends Dashable<R>> FloatingMultiChunkWriter<R, D> create(
			byte pos, DashRegistryWriter writer, WriteFailCallback<R, D> callback, List<DashObjectMetadata<R, D>> metadataList, Class<?> dashType) {
		var chunkWriter = new FloatingMultiChunkWriter<>(pos, writer, callback, dashType);

		for (DashObjectMetadata<R, D> metadata : metadataList) {
			chunkWriter.addMetadata(callback, metadata);
		}

		return chunkWriter;
	}

	private void addMetadata(WriteFailCallback<R, D> callback, DashObjectMetadata<R, D> metadata) {
		constructorMap.put(metadata.targetClass, new Creator<>(callback, DashConstructor.create(metadata.dashClass, metadata.targetClass), this));
		dashClasses.add(metadata.dashClass);
		rawClasses.add(metadata.targetClass);
	}

	@Override
	public int add(R object) {
		final int pos = rawList.size();
		rawList.add(object);
		return pos;
	}

	@Override
	public Collection<Class<?>> getClasses() {
		return rawClasses;
	}

	@Override
	public Collection<Class<?>> getDashClasses() {
		return dashClasses;
	}

	@Override
	public D[] writeOut(String taskName) {
		return DashThreading.runFloatingMultiWriteTask(taskName, rawList, callback, constructorMap, writer);
	}
}
