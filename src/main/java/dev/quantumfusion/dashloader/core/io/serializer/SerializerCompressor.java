package dev.quantumfusion.dashloader.core.io.serializer;

import io.airlift.compress.Compressor;
import io.airlift.compress.Decompressor;
import io.airlift.compress.lz4.Lz4Compressor;
import io.airlift.compress.lz4.Lz4Decompressor;
import io.airlift.compress.lzo.LzoCompressor;
import io.airlift.compress.lzo.LzoDecompressor;
import io.airlift.compress.snappy.SnappyCompressor;
import io.airlift.compress.snappy.SnappyDecompressor;
import io.airlift.compress.zstd.ZstdCompressor;
import io.airlift.compress.zstd.ZstdDecompressor;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public abstract class SerializerCompressor {

	@Nullable
	public static SerializerCompressor create(String name) {
		return switch (name.toUpperCase()) {
			case "LZ4" -> new WrappedCompressor(new Lz4Compressor(), new Lz4Decompressor());
			case "LZO" -> new WrappedCompressor(new LzoCompressor(), new LzoDecompressor());
			case "ZSTD" -> new WrappedCompressor(new ZstdCompressor(), new ZstdDecompressor());
			case "SNAPPY" -> new WrappedCompressor(new SnappyCompressor(), new SnappyDecompressor());
			case "RAW" -> null;
			default -> throw new RuntimeException("No compression algorithm " + name + " known. Options are LZ4, LZO, ZSTD, SNAPPY and RAW");
		};
	}

	public abstract int maxLength(int size);

	public abstract void compress(ByteBuffer input, ByteBuffer output);

	public abstract void decompress(ByteBuffer input, ByteBuffer output);

	private static class WrappedCompressor extends SerializerCompressor {
		private final Compressor compressor;
		private final Decompressor decompressor;

		public WrappedCompressor(Compressor compressor, Decompressor decompressor) {
			this.compressor = compressor;
			this.decompressor = decompressor;
		}


		public int maxLength(int size) {
			return compressor.maxCompressedLength(size);
		}

		public void compress(ByteBuffer input, ByteBuffer output) {
			compressor.compress(input, output);
		}

		public void decompress(ByteBuffer input, ByteBuffer output) {
			decompressor.decompress(input, output);
		}
	}
}
