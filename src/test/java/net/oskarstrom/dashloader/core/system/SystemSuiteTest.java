package net.oskarstrom.dashloader.core.system;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.oskarstrom.dashloader.core.DashLoaderCore;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.registry.DashRegistry;
import net.oskarstrom.dashloader.core.registry.regdata.MultiRegistryDataImpl;
import net.oskarstrom.dashloader.core.registry.regdata.MultiStageRegistryData;
import net.oskarstrom.dashloader.core.registry.regdata.RegistryData;
import net.oskarstrom.dashloader.core.registry.regdata.SoloRegistryDataImpl;
import net.oskarstrom.dashloader.core.serializer.DashSerializer;
import net.oskarstrom.dashloader.core.serializer.DashSerializerManager;
import net.oskarstrom.dashloader.core.system.dashobjects.*;
import net.oskarstrom.dashloader.core.system.objects.BakedModel;
import net.oskarstrom.dashloader.core.system.objects.Identifier;
import net.oskarstrom.dashloader.core.system.objects.MultiPartBakedModel;
import net.oskarstrom.dashloader.core.system.objects.Sprite;
import net.oskarstrom.dashloader.core.util.ClassLoaderHelper;
import net.oskarstrom.dashloader.core.util.DashHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SystemSuiteTest {
	public int test = 0;


	@Test
	public void serialization() {
		// data
		final DataTestThing dataTestThing = DataTestThing.create(10);


		final DashLoaderCore core = DashLoaderCore.create();
		core.withDashObjects(DashBasicBakedModel.class, DashIdentifier.class, DashMultiPartBakedModel.class, DashSprite.class, DashWeightedBakedModel.class);
		final DashRegistry registry = core.createDashRegistry();

		// write data
		IntList integers = new IntArrayList();
		for (BakedModel model : dataTestThing.models) {
			integers.add(registry.add(model));
		}

		// create serializable data
		var dashMappingData = new DashMappingData(integers.toIntArray());
		var registryData = new DashRegistryData(registry.exportStorage(DashModel.class),
												registry.exportStorage(DashIdentifier.class),
												registry.exportStorage(DashSprite.class));

		//create serializers
		var manager = core.createSerializationManager(Path.of("./test/").normalize());
		var registryDataSerializer = manager.loadOrCreateSerializer("RegistryData", DashRegistryData.class, Path.of("./test/thing/").normalize(), DashModel.class, RegistryData.class);
		var mappingSerializer = manager.loadOrCreateSerializer("ModelData", DashMappingData.class, Path.of("./test/thing/").normalize());

		try {
			registryDataSerializer.serialize(registryData);
			mappingSerializer.serialize(dashMappingData);

			final DashRegistryData i0 = registryDataSerializer.deserialize();
			final DashMappingData i1 = mappingSerializer.deserialize();
			System.out.println(i0.equals(registryData));
			System.out.println(i1.equals(dashMappingData));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deserialization() throws IOException {
		ClassLoaderHelper.init();
		DashSerializerManager manager = new DashSerializerManager(Path.of("./test/").normalize());
		manager.addSubclasses(RegistryData.class, SoloRegistryDataImpl.class, MultiRegistryDataImpl.class, MultiStageRegistryData.class);
		manager.addSubclasses(DashModel.class, DashBasicBakedModel.class, DashMultiPartBakedModel.class, DashWeightedBakedModel.class);
		final DashSerializer<DashRegistryData> registryDataSerializer = manager.loadOrCreateSerializer("RegistryData", DashRegistryData.class, Path.of("./test/thing/").normalize(), DashModel.class, RegistryData.class);
		final DashSerializer<DashMappingData> mappingSerializer = manager.loadOrCreateSerializer("ModelData", DashMappingData.class, Path.of("./test/thing/").normalize());
		final DashRegistryData i0 = registryDataSerializer.deserialize();
		final DashMappingData i1 = mappingSerializer.deserialize();
	}


	public record DataTestThing(List<BakedModel> models) {

		public static DataTestThing create(int size) {
			ArrayList<BakedModel> bakedModels = new ArrayList<BakedModel>();
			for (int i = 0; i < size; i++) {
				bakedModels.add(MultiPartBakedModel.create(bakedModels));
			}
			return new DataTestThing(bakedModels);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			DataTestThing dataTestThing = (DataTestThing) o;

			return Objects.equals(models, dataTestThing.models);
		}

		@Override
		public int hashCode() {
			return models != null ? models.hashCode() : 0;
		}
	}

	@Data
	public static class DashMappingData {
		public final int[] models;

		public DashMappingData(int[] models) {
			this.models = models;
		}


		public DashMappingData(DataTestThing dataTestThing, DashRegistry registry) {
			final List<BakedModel> models = dataTestThing.models;
			final int[] out = new int[models.size()];
			for (int i = 0, inputLength = models.size(); i < inputLength; i++) {
				out[i] = registry.add(models.get(i));
			}
			this.models = out;
		}

		public DataTestThing export(DashExportHandler handler) {
			final BakedModel[] arrayFromRegistry = DashHelper.getArrayFromRegistry(models, new BakedModel[models.length], handler);
			return new DataTestThing(Arrays.asList(arrayFromRegistry));
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			DashMappingData that = (DashMappingData) o;
			return Arrays.equals(models, that.models);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(models);
		}
	}


	@Data
	public record DashRegistryData(
			RegistryData<BakedModel, DashModel> modelData,
			RegistryData<Identifier, DashIdentifier> identifierData,
			RegistryData<Sprite, DashSprite> spriteData) {
	}

}
