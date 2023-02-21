package me.jellysquid.mods.radon.mixin;

import me.jellysquid.mods.radon.common.ChunkDatabaseAccess;
import me.jellysquid.mods.radon.common.db.LMDBInstance;
import net.minecraft.world.storage.SerializingRegionBasedStorage;
import net.minecraft.world.storage.StorageIoWorker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SerializingRegionBasedStorage.class)
public class MixinSerializingRegionBasedStorage implements ChunkDatabaseAccess {
    @Shadow
    @Final
    private StorageIoWorker worker;

    @Override
    public void setDatabase(LMDBInstance database) {
        ((ChunkDatabaseAccess) this.worker).setDatabase(database);
    }
}