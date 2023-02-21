package me.jellysquid.mods.radon.mixin;

import me.jellysquid.mods.radon.common.ChunkDatabaseAccess;
import me.jellysquid.mods.radon.common.db.LMDBInstance;
import net.minecraft.world.storage.StorageIoWorker;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VersionedChunkStorage.class)
public class MixinVersionedChunkStorage implements ChunkDatabaseAccess {
    @Shadow
    @Final
    private StorageIoWorker worker;

    @Override
    public void setDatabase(LMDBInstance database) {
        ((ChunkDatabaseAccess) this.worker).setDatabase(database);
    }
}