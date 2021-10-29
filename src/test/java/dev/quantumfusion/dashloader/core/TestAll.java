package dev.quantumfusion.dashloader.core;

import dev.quantumfusion.dashloader.core.objects.Identifier;
import dev.quantumfusion.dashloader.core.objects.IdentifierDash;
import dev.quantumfusion.dashloader.core.objects.model.*;
import dev.quantumfusion.dashloader.core.registry.ChunkDataHolder;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class TestAll {

	@Test
	void name() {
		// mc data
		System.out.println("Data creation");
		VanillaData data = new VanillaData();
		data.fill();


		System.out.println("Core init");
		DashLoaderCore core = new DashLoaderCore(Path.of("./testing"), IdentifierDash.class, BakedModelDash.class, HoldingBakedModelDash.class, HoldingHoldingBakedModelDash.class);
		System.out.println("Subcache");
		core.setCurrentSubcache("main");

		System.out.println("RegistryDataHolder serializer");
		core.prepareSerializer(RegistryDataHolder.class, ModelDash.class, IdentifierDash.class);
		System.out.println("RegistryMappings serializer");
		core.prepareSerializer(RegistryMappings.class);

		if (core.isCacheMissing()) {
			System.out.println("Creating Writer");
			var writer = core.createWriter();

			System.out.println("Writing");
			// our registryMappings
			var registryMappings = new RegistryMappings();
			for (var model : data.models) {
				registryMappings.models.add(writer.add(model));
			}


			System.out.println("Exporting");
			// export the chunks
			var identifierData = writer.getChunk(IdentifierDash.class).exportData();
			var modelData = writer.getChunk(ModelDash.class).exportData();
			final RegistryDataHolder registryDataHolder = new RegistryDataHolder(identifierData, modelData);

			System.out.println("Saving");
			core.save(registryDataHolder);
			core.save(registryMappings);
		} else {
			System.out.println("Loading cache");

			// loads from file
			System.out.println("Read from file Data");
			final RegistryDataHolder registryDataHolder = core.load(RegistryDataHolder.class);
			System.out.println("Read from file Mappings");
			final RegistryMappings registryMappings = core.load(RegistryMappings.class);

			System.out.println("Create reader");
			final DashRegistryReader reader = core.createReader(registryDataHolder);

			System.out.println("Acquire info");
			VanillaData dataOut = new VanillaData();
			for (Integer model : registryMappings.models) {
				dataOut.models.add(reader.get(model));
			}

			System.out.println(data.equals(dataOut));
		}
	}

	@Data
	public record RegistryDataHolder(
			AbstractDataChunk<Identifier, IdentifierDash> identifierData,
			AbstractDataChunk<Model, ModelDash> modelData) implements ChunkDataHolder {

		@Override
		public Collection<AbstractDataChunk<?, ?>> getChunks() {
			return List.of(identifierData, modelData);
		}
	}

	@Data
	public static class RegistryMappings {
		public List<Integer> models = new ArrayList<>();

		public RegistryMappings() {
		}

		public RegistryMappings(List<Integer> models) {
			this.models = models;
		}
	}

	public static class VanillaData {

		public final List<HoldingHoldingBakedModel> models = new ArrayList<>();


		public VanillaData() {
		}

		public void fill() {
			final Identifier fsdadfd = new Identifier("fsdadfd");

			for (int i = 0; i < 1000000; i++) {
				models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(i % 200, fsdadfd, "fdfsafsd" + (i % 200)), i)));
			}

		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			VanillaData data = (VanillaData) o;
			return Objects.equals(models, data.models);
		}

		@Override
		public int hashCode() {
			return Objects.hash(models);
		}

	}
}
