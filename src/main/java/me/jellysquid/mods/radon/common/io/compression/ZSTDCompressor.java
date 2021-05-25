package me.jellysquid.mods.radon.common.io.compression;

import jdk.incubator.foreign.MemorySegment;
import me.jellysquid.mods.radon.common.natives.NativeUtil;
import me.jellysquid.mods.radon.common.natives.Zstd;

import java.nio.ByteBuffer;

public class ZSTDCompressor implements StreamCompressor {
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

        MemorySegment dst;
        if (size == 0) {
            dst = MemorySegment.ofByteBuffer(ByteBuffer.allocateDirect(0)); // You seemingly can't allocate a 0-sized buffer directly. Despite the javadoc saying you can.
        } else {
            dst = NativeUtil.allocateNative(size);
        }
        checkError(Zstd.ZSTD_decompress(dst, src));

        return dst;
    }

    private static int checkError(int rc) {
        if (Zstd.ZSTD_isError(rc)) {
            throw new IllegalStateException(Zstd.ZSTD_getErrorName(rc));
        }

        return rc;
    }
}
