package dev.quantumfusion.dashloader.core;

import dev.quantumfusion.dashloader.core.objects.Identifier;
import dev.quantumfusion.dashloader.core.objects.IdentifierDash;
import dev.quantumfusion.dashloader.core.objects.model.*;
import dev.quantumfusion.dashloader.core.registry.ChunkDataHolder;
import dev.quantumfusion.dashloader.core.registry.DashRegistryBuilder;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.serializer.DashSerializer;
import dev.quantumfusion.dashloader.core.util.DashThreading;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class TestAll {

	@Test
	void name() {
		final DashRegistryWriter writer = DashRegistryBuilder.createWriter(IdentifierDash.class, BakedModelDash.class, HoldingBakedModelDash.class, HoldingHoldingBakedModelDash.class);

		VanillaData data = new VanillaData();
		data.fill();

		final Mappings mappings = new Mappings();
		for (HoldingHoldingBakedModel model : data.models) {
			mappings.models.add(writer.add(model));
		}

		var identifierData = writer.getChunk(IdentifierDash.class).exportData();
		var modelData = writer.getChunk(ModelDash.class).exportData();

		final RegistryDataHolder registryDataHolder = new RegistryDataHolder(identifierData, modelData);
		final DashSerializer<RegistryDataHolder> dataSerializer = DashSerializer.create(Path.of("./test/registry.data"), RegistryDataHolder.class, writer, ModelDash.class, IdentifierDash.class);
		final DashSerializer<Mappings> mapSerializer = DashSerializer.create(Path.of("./test/mappings.data"), Mappings.class, writer);


		try {
			dataSerializer.encode(registryDataHolder);
			mapSerializer.encode(mappings);
			final RegistryDataHolder dataHolder = dataSerializer.decode();
			final Mappings mappings1 = mapSerializer.decode();

			DashThreading.init();
			final DashRegistryReader reader = DashRegistryBuilder.createReader(dataHolder);
			reader.export();

			VanillaData dataOut = new VanillaData();
			for (Integer model : mappings1.models) {
				dataOut.models.add(reader.get(model));
			}

			System.out.println(dataOut.equals(data));
		} catch (IOException e) {
			e.printStackTrace();
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
	public static class Mappings {
		public List<Integer> models = new ArrayList<>();

		public Mappings() {
		}

		public Mappings(List<Integer> models) {
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
