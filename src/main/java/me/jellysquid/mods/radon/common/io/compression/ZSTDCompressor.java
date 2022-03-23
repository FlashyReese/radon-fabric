package me.jellysquid.mods.radon.common.io.compression;

import jdk.incubator.foreign.MemorySegment;
import me.jellysquid.mods.radon.common.natives.NativeUtil;
import me.jellysquid.mods.radon.common.natives.Zstd;

import java.nio.ByteBuffer;

public class ZSTDCompressor implements StreamCompressor {
    private static int checkError(int rc) {
        if (Zstd.ZSTD_isError(rc)) {
            throw new IllegalStateException(Zstd.ZSTD_getErrorName(rc));
        }

        return rc;
    }

    @Override
    public MemorySegment compress(MemorySegment src) {
        MemorySegment dst = NativeUtil.allocateNative(Zstd.ZSTD_compressBound(src.byteSize()));
        int newSize = checkError(me.jellysquid.mods.radon.common.natives.Zstd.ZSTD_compress(dst, src, 7));

        return dst.asSlice(0, newSize);
    }

    @Override
    public MemorySegment decompress(MemorySegment src) {
        long size = Zstd.ZSTD_getFrameContentSize(src);
        checkError((int)size);

        MemorySegment dst = NativeUtil.allocateNative(size);
        checkError(Zstd.ZSTD_decompress(dst, src));

        return dst;
    }
}
