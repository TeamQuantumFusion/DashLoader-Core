package dev.quantumfusion.dashloader.core.registry.chunk.write.impl;

import dev.quantumfusion.dashloader.core.DashObjectMetadata;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.Creator;
import dev.quantumfusion.dashloader.core.api.DashConstructor;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.WriteFailCallback;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.StagedDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.write.AbstractChunkWriter;
import dev.quantumfusion.dashloader.core.util.DashableEntry;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StagedMultiChunkWriter<R, D extends Dashable<R>> extends AbstractChunkWriter<R, D> {
	private final Object2ObjectLinkedOpenHashMap<Class<?>, List<DashableEntry<D>>> dashList = new Object2ObjectLinkedOpenHashMap<>();
	private final Object2ObjectMap<Class<R>, Creator<R, D>> constructorMap = new Object2ObjectOpenHashMap<>();
	private final List<Class<?>> dashClasses = new ArrayList<>();
	private final List<Class<?>> rawClasses = new ArrayList<>();
	private int objects = 0;

	protected StagedMultiChunkWriter(byte pos, DashRegistryWriter writer, WriteFailCallback<R, D> callback, Class<?> dashType) {
		super(pos, writer, callback, dashType);
	}


	public static <R, D extends Dashable<R>> StagedMultiChunkWriter<R, D> create(
			byte pos, DashRegistryWriter writer, WriteFailCallback<R, D> callback, List<DashObjectMetadata<R, D>> metadataList, Class<?> dashType) {
		var chunkWriter = new StagedMultiChunkWriter<>(pos, writer, callback, dashType);

		for (DashObjectMetadata<R, D> metadata : metadataList) {
			chunkWriter.addMetadata(callback, metadata);
		}

		// failed objects
		chunkWriter.dashList.put(WriteFailCallback.class, new ArrayList<>());
		return chunkWriter;
	}

	private void addMetadata(WriteFailCallback<R, D> callback, DashObjectMetadata<R, D> metadata) {
		constructorMap.put(metadata.targetClass, new Creator<>(callback, DashConstructor.create(metadata.dashClass, metadata.targetClass), this));
		dashClasses.add(metadata.dashClass);
		rawClasses.add(metadata.targetClass);
		dashList.put(metadata.dashClass, new ArrayList<>());
	}

	@Override
	public int add(R object) {
		final int pos = objects++;

		final Creator<R, D> rdDashConstructor = constructorMap.get(object.getClass());
		if (rdDashConstructor == null) {
			final D dashObj = callback.fallback(object, writer, new NullPointerException("Constructor not found for " + object.getClass().getSimpleName()));
			dashList.get(WriteFailCallback.class).add(new DashableEntry<>(pos, dashObj));
		} else {
			final D dashObj = rdDashConstructor.create(object, writer);
			dashList.get(dashObj.getClass()).add(new DashableEntry<>(pos, dashObj));
		}

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
	public AbstractDataChunk<R, D> exportData() {
		DashableEntry<D>[][] out = new DashableEntry[dashList.size()][];


		int i = 0;
		int size = 0;
		for (List<DashableEntry<D>> value : dashList.values()) {
			out[i++] = value.toArray(DashableEntry[]::new);
			size += value.size();
		}

		return new StagedDataChunk<>(pos, dashType.getSimpleName(), out, size);
	}

	@Override
	public D[] writeOut(String taskName) {
		throw new UnsupportedOperationException("no");
	}
}
