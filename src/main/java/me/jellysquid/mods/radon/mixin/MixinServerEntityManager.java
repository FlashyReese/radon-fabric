package me.jellysquid.mods.radon.mixin;

import me.jellysquid.mods.radon.common.ChunkDatabaseAccess;
import me.jellysquid.mods.radon.common.EntityChunkDataAccessor;
import me.jellysquid.mods.radon.common.db.LMDBInstance;
import me.jellysquid.mods.radon.common.db.spec.DatabaseSpec;
import me.jellysquid.mods.radon.common.db.spec.impl.WorldDatabaseSpecs;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.world.entity.EntityHandler;
import net.minecraft.world.storage.ChunkDataAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntityManager.class)
public class MixinServerEntityManager {
    @Unique
    private LMDBInstance storage;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Class entityClass, EntityHandler handler, ChunkDataAccess dataAccess, CallbackInfo ci) {
        this.storage = new LMDBInstance(((EntityChunkDataAccessor) dataAccess).getPath(), "entities", new DatabaseSpec[] {
                WorldDatabaseSpecs.CHUNK_DATA
        });

        ((ChunkDatabaseAccess) dataAccess).setDatabase(this.storage);
    }
}