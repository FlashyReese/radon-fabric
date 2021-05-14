package me.jellysquid.mods.radon.common.io.compression;

import org.lwjgl.util.zstd.Zstd;

import java.nio.ByteBuffer;

public class ZSTDCompressor implements StreamCompressor {
    @Override
    public ByteBuffer compress(ByteBuffer src) {
        ByteBuffer dst = ByteBuffer.allocateDirect(me.jellysquid.mods.radon.common.natives.Zstd.ZSTD_compressBound(src.remaining()));
        dst.limit(checkError(me.jellysquid.mods.radon.common.natives.Zstd.ZSTD_compress(dst, src, 7)));

        return dst;
    }

    @Override
    public ByteBuffer decompress(ByteBuffer src) {
        ByteBuffer dst = ByteBuffer.allocateDirect(checkError((int)Zstd.ZSTD_getFrameContentSize(src)));
        checkError((int)Zstd.ZSTD_decompress(dst, src));

        return dst;
    }

    private static int checkError(int rc) {
        if (me.jellysquid.mods.radon.common.natives.Zstd.ZSTD_isError(rc)) {
            throw new IllegalStateException(me.jellysquid.mods.radon.common.natives.Zstd.ZSTD_getErrorName(rc));
        }

        return rc;
    }
}
