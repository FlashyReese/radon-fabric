package me.jellysquid.mods.radon.common.db.spec.impl;

import me.jellysquid.mods.radon.common.db.spec.DatabaseSpec;
import me.jellysquid.mods.radon.common.io.compression.DefaultStreamCompressors;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;

public class WorldDatabaseSpecs {
    public static final DatabaseSpec<ChunkPos, NbtCompound> CHUNK_DATA =
            new DatabaseSpec<>("chunks", ChunkPos.class, NbtCompound.class, DefaultStreamCompressors.ZSTD, 8 * 1024 * 1024);

    public static final DatabaseSpec<ChunkPos, NbtCompound> POI =
            new DatabaseSpec<>("poi", ChunkPos.class, NbtCompound.class, DefaultStreamCompressors.ZSTD, 512 * 1024);
}
