package me.jellysquid.mods.radon.common.natives;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.Range;

import java.lang.invoke.MethodHandle;
import java.nio.ByteBuffer;

public class Zstd {
    private static final MethodHandle ZSTD_compressBound = NativeUtil.getHandle(
            "ZSTD_compressBound",
            Type.UINT,
            Type.UINT
    );
    private static final MethodHandle ZSTD_compress = NativeUtil.getHandle(
            "ZSTD_compress",
            Type.UINT,
            Type.POINTER, Type.UINT, Type.POINTER, Type.UINT, Type.UINT
    );
    private static final MethodHandle ZSTD_decompress = NativeUtil.getHandle(
            "ZSTD_decompress",
            Type.UINT,
            Type.POINTER, Type.UINT, Type.POINTER, Type.UINT
    );
    private static final MethodHandle ZSTD_getFrameContentSize = NativeUtil.getHandle(
            "ZSTD_getFrameContentSize",
            Type.LONG,
            Type.POINTER, Type.UINT
    );
    private static final MethodHandle ZSTD_isError = NativeUtil.getHandle(
            "ZSTD_isError",
            Type.UINT,
            Type.UINT
    );
    private static final MethodHandle ZSTD_getErrorName = NativeUtil.getHandle(
            "ZSTD_getErrorName",
            Type.POINTER,
            Type.UINT
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

    public static int ZSTD_decompress(ByteBuffer dst, ByteBuffer src) {
        try {
            var dstSegment = MemorySegment.ofByteBuffer(dst);
            var srcSegment = MemorySegment.ofByteBuffer(src);

            return (int)ZSTD_decompress.invokeExact(
                    dstSegment.address(),
                    (int)dstSegment.byteSize(),
                    srcSegment.address(),
                    (int)srcSegment.byteSize()
            );
        } catch (Throwable t) {
            throw new RuntimeException("Exception in Zstd", t);
        }
    }

    public static long ZSTD_getFrameContentSize(ByteBuffer src) {
        try {
            var srcSegment = MemorySegment.ofByteBuffer(src);

            return (long)ZSTD_getFrameContentSize.invokeExact(
                    srcSegment.address(),
                    (int)srcSegment.byteSize()
            );
        } catch (Throwable t) {
            throw new RuntimeException("Exception in Zstd", t);
        }
    }

    public static boolean ZSTD_isError(int code) {
        try {
            return ((int)ZSTD_isError.invokeExact(code) & 1) == 1;
        } catch (Throwable t) {
            throw new RuntimeException("Exception in Zstd", t);
        }
    }

    public static String ZSTD_getErrorName(int code) {
        try {
            var pointer = (MemoryAddress)ZSTD_getErrorName.invokeExact(code);
            return NativeUtil.pointerToString(pointer);
        } catch (Throwable t) {
            throw new RuntimeException("Exception in Zstd", t);
        }
    }
}