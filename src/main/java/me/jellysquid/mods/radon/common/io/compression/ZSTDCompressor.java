package me.jellysquid.mods.radon.common.io.compression;

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
    public ByteBuffer compress(ByteBuffer src) {
        ByteBuffer dst = ByteBuffer.allocateDirect(Zstd.ZSTD_compressBound(src.remaining()));
        dst.limit(checkError(Zstd.ZSTD_compress(dst, src, 7)));

        return dst;
    }

    @Override
    public ByteBuffer decompress(ByteBuffer src) {
        ByteBuffer dst = ByteBuffer.allocateDirect(checkError((int)Zstd.ZSTD_getFrameContentSize(src)));
        checkError(Zstd.ZSTD_decompress(dst, src));

        return dst;
    }
}
