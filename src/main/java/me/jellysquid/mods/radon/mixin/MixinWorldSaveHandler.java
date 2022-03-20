package me.jellysquid.mods.radon.mixin;

import com.mojang.datafixers.DataFixer;
import me.jellysquid.mods.radon.common.db.spec.impl.PlayerDatabaseSpecs;
import me.jellysquid.mods.radon.common.db.DatabaseItem;
import me.jellysquid.mods.radon.common.db.LMDBInstance;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.world.WorldSaveHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;

@SuppressWarnings("OverwriteAuthorRequired")
@Mixin(WorldSaveHandler.class)
public class MixinWorldSaveHandler implements DatabaseItem {

    @Shadow @Final protected DataFixer dataFixer;

    @Shadow @Final private static Logger LOGGER;
    private LMDBInstance storage;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/io/File;mkdirs()Z"))
    private boolean disableMkdirs(File file) {
        return true;
    }

    @Overwrite
    public void savePlayerData(PlayerEntity playerEntity) {
        try {
            this.storage
                    .getTransaction(PlayerDatabaseSpecs.PLAYER_DATA)
                    .add(playerEntity.getUuid(), playerEntity.writeNbt(new NbtCompound()));
        } catch (Exception e) {
            LOGGER.warn("Failed to save player data for {}", playerEntity.getName().getString());
        }
    }

    @Overwrite
    @Nullable
    public NbtCompound loadPlayerData(PlayerEntity playerEntity) {
        NbtCompound compoundTag = null;

        try {
            compoundTag = this.storage
                    .getDatabase(PlayerDatabaseSpecs.PLAYER_DATA)
                    .getValue(playerEntity.getUuid());
        } catch (Exception e) {
            LOGGER.warn("Failed to load player data for {}", playerEntity.getName().getString(), e);
        }

        if (compoundTag != null) {
            int i = compoundTag.contains("DataVersion", 3) ? compoundTag.getInt("DataVersion") : -1;
            playerEntity.saveNbt(NbtHelper.update(this.dataFixer, DataFixTypes.PLAYER, compoundTag, i));
        }

        return compoundTag;
    }


    @Override
    public void setStorage(LMDBInstance storage) {
        this.storage = storage;
    }

    @Override
    public LMDBInstance getStorage() {
        return this.storage;
    }
}
