package me.jellysquid.mods.radon.mixin;

import com.mojang.datafixers.DataFixer;
import me.jellysquid.mods.radon.common.ChunkDatabaseAccess;
import me.jellysquid.mods.radon.common.db.LMDBInstance;
import me.jellysquid.mods.radon.common.db.spec.impl.WorldDatabaseSpecs;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.FeatureUpdater;
import net.minecraft.world.storage.StorageIoWorker;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;

@SuppressWarnings("OverwriteAuthorRequired")
@Mixin(VersionedChunkStorage.class)
public class MixinVersionedChunkStorage implements ChunkDatabaseAccess {
    @Mutable
    @Shadow
    @Final
    private StorageIoWorker worker;

    @Shadow
    @Nullable
    private FeatureUpdater featureUpdater;

    private LMDBInstance storage;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void reinit(Path directory, DataFixer dataFixer, boolean dsync, CallbackInfo ci) {
        try {
            this.worker.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.worker = null;
    }

    @Overwrite
    public @Nullable NbtCompound getNbt(ChunkPos chunkPos) {
        return this.storage.getDatabase(WorldDatabaseSpecs.CHUNK_DATA)
                .getValue(chunkPos);
    }

    @Overwrite
    public void setNbt(ChunkPos chunkPos, NbtCompound compoundTag) {
        this.storage
                .getTransaction(WorldDatabaseSpecs.CHUNK_DATA)
                .add(chunkPos, compoundTag);

        if (this.featureUpdater != null) {
            this.featureUpdater.markResolved(chunkPos.toLong());
        }
    }

    @Overwrite
    public void completeAll() {

    }

    @Overwrite
    public void close() {

    }

    @Override
    public void setDatabase(LMDBInstance database) {
        this.storage = database;
    }
}
