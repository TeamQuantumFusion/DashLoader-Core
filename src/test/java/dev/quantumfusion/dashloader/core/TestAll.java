package dev.quantumfusion.dashloader.core;

import dev.quantumfusion.dashloader.core.objects.Identifier;
import dev.quantumfusion.dashloader.core.objects.IdentifierDash;
import dev.quantumfusion.dashloader.core.objects.holder.HoldingHoldingHoldingModel;
import dev.quantumfusion.dashloader.core.objects.holder.HoldingHoldingHoldingModelDash;
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


		DashLoaderCore core = new DashLoaderCore(Path.of("./testing"),
												 IdentifierDash.class,
												 BakedModelDash.class,
												 HoldingBakedModelDash.class,
												 HoldingHoldingBakedModelDash.class,
												 HoldingHoldingHoldingModelDash.class);
		core.setCurrentSubcache("main");

		core.prepareSerializer(RegistryDataHolder.class, HoldingHoldingHoldingModelDash.class, ModelDash.class, IdentifierDash.class);
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

			for (var model : data.whatAmIDoingWithMyLife) {
				registryMappings.extra.add(writer.add(model));
			}


			System.out.println("Exporting");
			// export the chunks
			var holder = writer.getChunk(HoldingHoldingHoldingModelDash.class).exportData();
			var identifierData = writer.getChunk(IdentifierDash.class).exportData();
			var modelData = writer.getChunk(ModelDash.class).exportData();
			final RegistryDataHolder registryDataHolder = new RegistryDataHolder(holder, identifierData, modelData);

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

			for (var model : registryMappings.extra) {
				dataOut.whatAmIDoingWithMyLife.add(reader.get(model));
			}

			System.out.println(data.equals(dataOut));
		}
	}

	@Data
	public record RegistryDataHolder(
			AbstractDataChunk<HoldingHoldingHoldingModel, HoldingHoldingHoldingModelDash> extra,
			AbstractDataChunk<Identifier, IdentifierDash> identifierData,
			AbstractDataChunk<Model, ModelDash> modelData) implements ChunkDataHolder {

		@Override
		public Collection<AbstractDataChunk<?, ?>> getChunks() {
			return List.of(identifierData, modelData, extra);
		}
	}

	@Data
	public static class RegistryMappings {
		public List<Integer> models = new ArrayList<>();
		public List<Integer> extra = new ArrayList<>();

		public RegistryMappings() {
		}

		public RegistryMappings(List<Integer> models, List<Integer> extra) {
			this.models = models;
			this.extra = extra;
		}
	}

	public static class VanillaData {
		public final List<HoldingHoldingBakedModel> models = new ArrayList<>();
		public final List<HoldingHoldingHoldingModel> whatAmIDoingWithMyLife = new ArrayList<>();


		public VanillaData() {
		}

		public void fill() {
			final Identifier fsdadfd = new Identifier("fsdadfd");
			for (int i = 0; i < 10; i++) {
				models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(i % 200, fsdadfd, "fdfsafsd" + (i % 200)), i)));
			}

			for (int i = 0; i < 10; i++) {
				whatAmIDoingWithMyLife.add(new HoldingHoldingHoldingModel(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(i % 230, fsdadfd, "fdfsafsd" + (i % 200)), i))));
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
