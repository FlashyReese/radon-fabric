package me.jellysquid.mods.radon.common.io.compression;

import jdk.incubator.foreign.MemorySegment;

public interface StreamCompressor {
    MemorySegment compress(MemorySegment in);

    MemorySegment decompress(MemorySegment in);
}
