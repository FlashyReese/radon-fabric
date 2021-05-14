package me.jellysquid.mods.radon.common.natives;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.Range;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;

public class Zstd {
    private static final MethodHandle ZSTD_compressBound = NativeUtil.getHandle(
            NativeUtil.LibZSTD,
            "ZSTD_compressBound",
            MethodType.methodType(int.class, int.class),
            FunctionDescriptor.of(CLinker.C_INT, CLinker.C_INT)
    );
    private static final MethodHandle ZSTD_compress = NativeUtil.getHandle(
            NativeUtil.LibZSTD,
            "ZSTD_compress",
            MethodType.methodType(int.class, MemoryAddress.class, int.class, MemoryAddress.class, int.class, int.class),
            FunctionDescriptor.of(CLinker.C_INT, CLinker.C_POINTER, CLinker.C_INT, CLinker.C_POINTER, CLinker.C_INT, CLinker.C_INT)
    );

    public static int ZSTD_compressBound(@Range(from = 0, to = Integer.MAX_VALUE) int maxSrc) {
        try {
            return (int)ZSTD_compressBound.invokeExact(maxSrc);
        } catch (Throwable t) {
            throw new RuntimeException("Exception in Zstd", t);
        }
    }

    public static int ZSTD_compress(ByteBuffer dst, ByteBuffer src, int level) {
        try {
            var dstSegment = MemorySegment.ofByteBuffer(dst);
            var srcSegment = MemorySegment.ofByteBuffer(src);

            return (int)ZSTD_compress.invokeExact(
                    dstSegment.address(),
                    (int)dstSegment.byteSize(),
                    srcSegment.address(),
                    (int)srcSegment.byteSize(),
                    level
            );
        } catch (Throwable t) {
            throw new RuntimeException("Exception in Zstd", t);
        }
    }
}
