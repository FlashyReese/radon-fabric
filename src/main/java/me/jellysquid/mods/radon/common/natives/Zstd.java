package me.jellysquid.mods.radon.common.natives;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import java.lang.invoke.MethodHandle;

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

    public static int ZSTD_compressBound(long maxSrc) {
        try {
            return (int)ZSTD_compressBound.invokeExact(NativeUtil.Long2Uint(maxSrc));
        } catch (Throwable t) {
            throw new RuntimeException("Exception in Zstd", t);
        }
    }

    public static int ZSTD_compress(MemorySegment dst, MemorySegment src, int level) {
        try {
            return (int)ZSTD_compress.invokeExact(
                    dst.address(),
                    NativeUtil.Long2Uint(dst.byteSize()),
                    src.address(),
                    NativeUtil.Long2Uint(src.byteSize()),
                    level
            );
        } catch (Throwable t) {
            throw new RuntimeException("Exception in Zstd", t);
        }
    }

    public static int ZSTD_decompress(MemorySegment dst, MemorySegment src) {
        try {
            return (int)ZSTD_decompress.invokeExact(
                    dst.address(),
                    (int)dst.byteSize(),
                    src.address(),
                    (int)src.byteSize()
            );
        } catch (Throwable t) {
            throw new RuntimeException("Exception in Zstd", t);
        }
    }

    public static long ZSTD_getFrameContentSize(MemorySegment src) {
        try {
            return (long)ZSTD_getFrameContentSize.invokeExact(
                    src.address(),
                    (int)src.byteSize()
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