package net.oskarstrom.dashloader.core.system;

import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.registry.DashExportHandler;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.DashRegistryBuilder;
import net.oskarstrom.dashloader.core.system.dashobjects.*;
import net.oskarstrom.dashloader.core.system.objects.BakedModel;
import net.oskarstrom.dashloader.core.system.objects.MultiPartBakedModel;
import net.oskarstrom.dashloader.core.util.DashHelper;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SystemSuiteTest {
	public int test = 0;


	@Test
	@Order(1)
	public void serialization() {
		final Data data = Data.create(10);

		final DashRegistryBuilder registryStorageFactory = DashRegistryBuilder.create();
		registryStorageFactory.withDashClasses(DashBasicBakedModel.class, DashIdentifier.class, DashMultiPartBakedModel.class, DashSprite.class, DashWeightedBakedModel.class);
		final DashRegistry registry = registryStorageFactory.build();
	}

	@Test
	@Order(2)
	public void deserialization() {
		System.out.println(test);
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
	}

	public static class DashMappingData {
		@Serialize
		public final int[] models;

		public DashMappingData(int[] models) {
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
			final BakedModel[] arrayFromRegistry = DashHelper.getArrayFromRegistry(models, handler);
			return new Data(Arrays.asList(arrayFromRegistry));
		}
	}


}
