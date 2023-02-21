package me.jellysquid.mods.radon;

import me.jellysquid.mods.radon.common.dep.DependencyExtractor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class RadonMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // We should bundle natives and load them but for the meanwhile :)
        // Using FFM API
        // -Dforeign.restricted=permit --add-modules jdk.incubator.foreign --enable-native-access=ALL-UNNAMED
        System.loadLibrary("zstd");
        System.loadLibrary("lmdb");
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            loadNatives();
        }
    }

    private void loadNatives() {
        DependencyExtractor.installLwjglNatives("lwjgl-lmdb", "3.2.2");
        DependencyExtractor.installLwjglNatives("lwjgl-zstd", "3.2.2");
    }
}
