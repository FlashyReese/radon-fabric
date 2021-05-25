package me.jellysquid.mods.radon.common.io.compression;

import jdk.incubator.foreign.MemorySegment;

public class DefaultStreamCompressors {
    public static final StreamCompressor NONE = new StreamCompressor() {
        @Override
        public MemorySegment compress(MemorySegment in) {
            return in;
        }

        @Override
        public MemorySegment decompress(MemorySegment in) {
            return in;
        }
    };

    public static final StreamCompressor ZSTD = new ZSTDCompressor();
}
