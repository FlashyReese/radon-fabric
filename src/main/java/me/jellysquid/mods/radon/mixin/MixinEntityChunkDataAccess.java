package me.jellysquid.mods.radon.mixin;

import com.mojang.datafixers.DataFixer;
import me.jellysquid.mods.radon.common.ChunkDatabaseAccess;
import me.jellysquid.mods.radon.common.EntityChunkDataAccessor;
import me.jellysquid.mods.radon.common.db.LMDBInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.storage.EntityChunkDataAccess;
import net.minecraft.world.storage.StorageIoWorker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.concurrent.Executor;

@Mixin(EntityChunkDataAccess.class)
public class MixinEntityChunkDataAccess implements ChunkDatabaseAccess, EntityChunkDataAccessor {
    @Shadow
    @Final
    private StorageIoWorker dataLoadWorker;

    @Unique
    private Path path;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(ServerWorld world, Path path, DataFixer dataFixer, boolean dsync, Executor executor, CallbackInfo ci) {
        this.path = path;
    }

    @Override
    public void setDatabase(LMDBInstance database) {
        ((ChunkDatabaseAccess) this.dataLoadWorker).setDatabase(database);
    }

    @Override
    public Path getPath() {
        return this.path;
    }
}
