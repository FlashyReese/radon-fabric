package me.jellysquid.mods.radon.mixin;

import com.mojang.datafixers.util.Either;
import me.jellysquid.mods.radon.common.ChunkDatabaseAccess;
import me.jellysquid.mods.radon.common.db.LMDBInstance;
import me.jellysquid.mods.radon.common.db.spec.impl.WorldDatabaseSpecs;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionBasedStorage;
import net.minecraft.world.storage.StorageIoWorker;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(StorageIoWorker.class)
public abstract class MixinStorageIoWorker implements ChunkDatabaseAccess {

    @Shadow
    protected abstract <T> CompletableFuture<T> run(Supplier<Either<T, Exception>> task);

    @Shadow
    @Final
    private Map<ChunkPos, StorageIoWorker.Result> results;

    @Shadow
    @Final
    private static Logger LOGGER;

    private LMDBInstance storage;

    /**
     * @author Mo0dss
     * @reason Temp solution for stupid lambda redirection
     */
    @Overwrite
    public CompletableFuture<Optional<NbtCompound>> readChunkData(ChunkPos pos) {
        return this.run(() -> {
            StorageIoWorker.Result result = this.results.get(pos);
            if(result == null) {
                try {
                    NbtCompound data = this.storage
                            .getDatabase(WorldDatabaseSpecs.CHUNK_DATA)
                            .getValue(pos);

                    return Either.left(Optional.ofNullable(data));
                } catch (Exception ex) {
                    LOGGER.warn("Failed to read chunk {}", pos, ex);
                    return Either.right(ex);
                }
            }

            return Either.left(Optional.ofNullable(result.nbt));
        });
    }

    /**
     * @author Mo0dss
     * @reason Temp solution for stupid lambda redirection
     */
    @Overwrite
    public CompletableFuture<Void> completeAll(boolean sync) {
        return this.run(() -> {
            return Either.left(CompletableFuture.allOf(this.results.values().stream().map(result -> result.future).toArray(CompletableFuture[]::new)));
        }).thenCompose(Function.identity()).thenCompose(identity -> {
            return this.run(() -> {
                if(!sync) {
                    return Either.left(null);
                }

                try {
                    this.storage.flushChanges();
                    return Either.left(null);
                } catch (Exception ex) {
                    LOGGER.warn("Failed to synchronize chunks", ex);
                    return Either.right(ex);
                }
            });
        });
    }

    /**
     * @author Mo0dss
     * @reason Temp solution for stupid lambda redirection
     */
    @Overwrite
    public CompletableFuture<Void> scanChunk(ChunkPos pos, NbtScanner scanner) {
        return this.run(() -> {
            StorageIoWorker.Result result = this.results.get(pos);
            if(result == null) {
                try {
                    this.storage.getDatabase(WorldDatabaseSpecs.CHUNK_DATA).scan(pos, scanner);
                    return Either.left(null);
                } catch (Exception ex) {
                    LOGGER.warn("Failed to bulk scan chunk {}", pos, ex);
                    return Either.right(ex);
                }
            }

            if(result.nbt != null) {
                result.nbt.accept(scanner);
            }

            return Either.left(null);
        });
    }

    @Redirect(method = "write",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/storage/RegionBasedStorage;write(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)V"
            )
    )
    private void onWrite$write(RegionBasedStorage instance, ChunkPos pos, NbtCompound nbt) {
        this.storage
                .getTransaction(WorldDatabaseSpecs.CHUNK_DATA)
                .add(pos, nbt);
    }

    @Redirect(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/RegionBasedStorage;close()V"))
    private void onClose(RegionBasedStorage instance) {
        this.storage.close();
    }

    @Override
    public void setDatabase(LMDBInstance database) {
        this.storage = database;
    }
}