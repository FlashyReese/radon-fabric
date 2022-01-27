package me.jellysquid.mods.radon.common.db.spec.impl;

import me.jellysquid.mods.radon.common.db.spec.DatabaseSpec;
import me.jellysquid.mods.radon.common.io.compression.DefaultStreamCompressors;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class PlayerDatabaseSpecs {
    public static final DatabaseSpec<UUID, String> ADVANCEMENTS =
            new DatabaseSpec<>("advancements", UUID.class, String.class, DefaultStreamCompressors.ZSTD, 128 * 1024);

    public static final DatabaseSpec<UUID, String> STATISTICS =
            new DatabaseSpec<>("statistics", UUID.class, String.class, DefaultStreamCompressors.ZSTD, 128 * 1024);

    public static final DatabaseSpec<UUID, NbtCompound> PLAYER_DATA =
            new DatabaseSpec<>("player_data", UUID.class, NbtCompound.class, DefaultStreamCompressors.ZSTD, 128 * 1024);
}
