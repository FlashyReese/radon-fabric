package me.jellysquid.mods.radon.mixin;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import me.jellysquid.mods.radon.common.ChunkDatabaseAccess;
import me.jellysquid.mods.radon.common.db.LMDBInstance;
import me.jellysquid.mods.radon.common.db.spec.impl.WorldDatabaseSpecs;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.storage.SerializingRegionBasedStorage;
import net.minecraft.world.storage.StorageIoWorker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(SerializingRegionBasedStorage.class)
public class MixinSerializingRegionBasedStorage<R> implements ChunkDatabaseAccess {
    @Mutable
    @Shadow
    @Final
    private StorageIoWorker worker;

    private LMDBInstance storage;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void reinit(Path path, Function<Runnable, Codec<R>> codecFactory, Function<Runnable, R> factory, DataFixer dataFixer, DataFixTypes dataFixTypes, boolean dsync, HeightLimitView world, CallbackInfo ci) {
        try {
            this.worker.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.worker = null;
    }

    @Override
    public void setDatabase(LMDBInstance storage) {
        this.storage = storage;
    }

    @Redirect(method = "loadNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/StorageIoWorker;getNbt(Lnet/minecraft/util/math/ChunkPos;)Lnet/minecraft/nbt/NbtCompound;"))
    private NbtCompound redirectLoadNbt(StorageIoWorker storageIoWorker, ChunkPos pos) {
        return this.storage
                .getDatabase(WorldDatabaseSpecs.POI)
                .getValue(pos);
    }

    @Redirect(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/StorageIoWorker;setResult(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Void> redirectSaveNbt(StorageIoWorker storageIoWorker, ChunkPos pos, NbtCompound nbt) {
        this.storage
                .getTransaction(WorldDatabaseSpecs.CHUNK_DATA)
                .add(pos, nbt);

        return null;
    }

    @Redirect(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/StorageIoWorker;close()V"))
    private void redirectClose(StorageIoWorker storageIoWorker) {

    }
}
