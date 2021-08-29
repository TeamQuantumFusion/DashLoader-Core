package net.oskarstrom.dashloader.core.system;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.oskarstrom.dashloader.core.DashLoaderFactory;
import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.registry.DashRegistry;
import net.oskarstrom.dashloader.core.registry.DashRegistryBuilder;
import net.oskarstrom.dashloader.core.registry.export.ExportData;
import net.oskarstrom.dashloader.core.serializer.DashSerializer;
import net.oskarstrom.dashloader.core.serializer.DashSerializerManager;
import net.oskarstrom.dashloader.core.system.dashobjects.*;
import net.oskarstrom.dashloader.core.system.objects.BakedModel;
import net.oskarstrom.dashloader.core.system.objects.MultiPartBakedModel;
import net.oskarstrom.dashloader.core.util.ClassLoaderHelper;
import net.oskarstrom.dashloader.core.util.DashHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SystemSuiteTest {
	public int test = 0;


	@Test
	public void serialization() {
		final Data data = Data.create(10);
		final DashRegistryBuilder registryStorageFactory = DashRegistryBuilder.create();
		ClassLoaderHelper.init();

		registryStorageFactory.withDashObjects(DashBasicBakedModel.class, DashIdentifier.class, DashMultiPartBakedModel.class, DashSprite.class, DashWeightedBakedModel.class);
		final DashRegistry registry = registryStorageFactory.build();
		IntList integers = new IntArrayList();
		for (BakedModel model : data.models) {
			integers.add(registry.add(model));
		}


		final ExportData<DashModel, Dashable<DashModel>> modelData = registry.getStorage(DashModel.class).getExportData();
		final ExportData<DashIdentifier, Dashable<DashIdentifier>> identifierData = registry.getStorage(DashIdentifier.class).getExportData();
		final ExportData<DashSprite, Dashable<DashSprite>> spriteData = registry.getStorage(DashSprite.class).getExportData();


		final DashExportHandler exportHandler = DashLoaderFactory.createExportHandler(3);
		System.out.println(modelData + " / " + modelData.getPos());
		System.out.println(identifierData + " / " + identifierData.getPos());
		System.out.println(spriteData + " / " + spriteData.getPos());

		exportHandler.addStorage(modelData);
		exportHandler.addStorage(identifierData);
		exportHandler.addStorage(spriteData);


		DashSerializerManager manager = new DashSerializerManager(Path.of("./test/").normalize());
		manager.addSubclasses(DashModel.class, DashBasicBakedModel.class, DashMultiPartBakedModel.class, DashWeightedBakedModel.class);
		final DashSerializer<DashRegistryData> registryDataSerializer = manager.loadOrCreateSerializer("RegistryData", DashRegistryData.class, Path.of("./test/thing/").normalize(), DashModel.class);
		final DashSerializer<DashMappingData> mappingSerializer = manager.loadOrCreateSerializer("ModelData", DashMappingData.class, Path.of("./test/thing/").normalize());

		final DashMappingData dashMappingData = new DashMappingData(integers.toIntArray());
		final DashRegistryData registryData = new DashRegistryData(modelData, identifierData, spriteData);
		try {
			registryDataSerializer.serialize(null, registryData);
			mappingSerializer.serialize(null, dashMappingData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deserialization() {
/*		ThreadManager.init();
		exportHandler.export();

		List<BakedModel> models = new ArrayList<>();
		for (Integer integer : integers) {
			models.add(exportHandler.get(integer));
		}
		System.out.println("don");

		Data nextLaunchData = new Data(models);
		System.out.println("Checking " + models.size() + " models if they are matching. Result: ");
		System.out.println("  -  " + nextLaunchData.equals(data));
		if (!nextLaunchData.equals(data)) {
			fail();
		}*/
	}


	public static class Data {
		public final List<BakedModel> models;

		public Data(List<BakedModel> models) {
			this.models = models;
		}

		public static Data create(int size) {
			ArrayList<BakedModel> bakedModels = new ArrayList<BakedModel>();
			for (int i = 0; i < size; i++) {
				bakedModels.add(MultiPartBakedModel.create(bakedModels));
			}
			return new Data(bakedModels);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Data data = (Data) o;

			return models != null ? models.equals(data.models) : data.models == null;
		}

		@Override
		public int hashCode() {
			return models != null ? models.hashCode() : 0;
		}
	}

	public static class DashMappingData {
		@Serialize
		public final int[] models;

		public DashMappingData(@Deserialize("models") int[] models) {
			this.models = models;
		}


		public DashMappingData(Data data, DashRegistry registry) {
			final List<BakedModel> models = data.models;
			final int[] out = new int[models.size()];
			for (int i = 0, inputLength = models.size(); i < inputLength; i++) {
				out[i] = registry.add(models.get(i));
			}
			this.models = out;
		}

		public Data export(DashExportHandler handler) {
			final BakedModel[] arrayFromRegistry = DashHelper.getArrayFromRegistry(models, new BakedModel[models.length], handler);
			return new Data(Arrays.asList(arrayFromRegistry));
		}
	}


	public static class DashRegistryData {
		@Serialize
		@SerializeSubclasses(path = 0)
		public ExportData<DashModel, Dashable<DashModel>> modelData;

		@Serialize
		public ExportData<DashIdentifier, Dashable<DashIdentifier>> identifierData;

		@Serialize
		public ExportData<DashSprite, Dashable<DashSprite>> spriteData;

		public DashRegistryData(@Deserialize("modelData") ExportData<DashModel, Dashable<DashModel>> modelData,
								@Deserialize("identifierData") ExportData<DashIdentifier, Dashable<DashIdentifier>> identifierData,
								@Deserialize("spriteData") ExportData<DashSprite, Dashable<DashSprite>> spriteData) {
			this.modelData = modelData;
			this.identifierData = identifierData;
			this.spriteData = spriteData;
		}
	}

}
