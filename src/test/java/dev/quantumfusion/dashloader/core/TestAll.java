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
		VanillaData data = new VanillaData();
		data.fill();


		DashLoaderCore core = new DashLoaderCore(Path.of("./testing"), IdentifierDash.class, BakedModelDash.class, HoldingBakedModelDash.class, HoldingHoldingBakedModelDash.class);
		core.setCurrentSubcache("main");
		core.prepareSerializer(RegistryDataHolder.class, ModelDash.class, IdentifierDash.class);
		core.prepareSerializer(RegistryMappings.class);

		if (core.isCacheMissing()) {
			System.out.println("Caching");
			var writer = core.createWriter();

			// our registryMappings
			var registryMappings = new RegistryMappings();
			for (var model : data.models) {
				registryMappings.models.add(writer.add(model));
			}


			// export the chunks
			var identifierData = writer.getChunk(IdentifierDash.class).exportData();
			var modelData = writer.getChunk(ModelDash.class).exportData();
			final RegistryDataHolder registryDataHolder = new RegistryDataHolder(identifierData, modelData);

			core.save(registryDataHolder);
			core.save(registryMappings);
		} else {
			System.out.println("Loading cache");

			// loads from file
			final RegistryDataHolder registryDataHolder = core.load(RegistryDataHolder.class);
			final RegistryMappings registryMappings = core.load(RegistryMappings.class);

			final DashRegistryReader reader = core.createReader(registryDataHolder);
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
			final HoldingHoldingBakedModel fdfsafsd = new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, fsdadfd, "fdfsafsd"), 432));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, fsdadfd, "fdfsafsd"), 432342)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, fsdadfd, "fdfsdsafasdfafsd"), 433422)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, fsdadfd, "fdfsdafsafsd"), 432)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, fsdadfd, "fdffdssafsd"), 43442)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdafdsdfd"), "fdfsdfasafsd"), 4342)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, fsdadfd, "fdfsasfdafsd"), 4232)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, fsdadfd, "fdfsafsd"), 4432)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdsdfsdfadfd"), "fdfsafsd"), 4382)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdadasdfasdffd"), "fdfsafsd"), 442332)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdsdfadfd"), "fdfsafsd"), 435642)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsddadfd"), "fdfsafsd"), 432)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdsdfsdfadfd"), "fdfsafsd"), 432)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdsddfsdfasdfasdfdfd"), "fdfsafsd"), 4132)));
			models.add(fdfsafsd);
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdasdfasdfdfd"), "fdfsafsd"), 4366662)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, fsdadfd, "fdfsaafdfsd"), 432)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdasdfdfd"), "fdfsasdfaasdffsd"), 466666632)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdfasdadfd"), "fdfsafsd"), 432)));
			models.add(fdfsafsd);
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("glisco sucks"), "fdfsafsdfaasdfsd"), 43692)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdafasdfdfd"), "fdfsafsd"), 46932)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdaddfasfd"), "fdfsafsd"), 432)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, fsdadfd, "fdfsasdfsdffsd"), 432)));
			models.add(fdfsafsd);
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdafdsdfd"), "fdfsasdfsdfafsd"), 420)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdadfdfdfd"), "fdfsafsd"), 432)));
			models.add(fdfsafsd);
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdasdfadfd"), "fdfsafsd"), 432)));
			models.add(fdfsafsd);
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdaddsfasdffd"), "fdfsafsd"), 432)));
			models.add(fdfsafsd);
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, fsdadfd, "fdfsasdfasdffsd"), 432)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, fsdadfd, "fdfsaasfdfsd"), 432)));
			models.add(fdfsafsd);
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, new Identifier("fsdadsdfsdfafd"), "fdfasfdsafsd"), 432)));
			models.add(new HoldingHoldingBakedModel(new HoldingBakedModel(new BakedModel(432, fsdadfd, "fdfsasdfafsd"), 432)));
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
