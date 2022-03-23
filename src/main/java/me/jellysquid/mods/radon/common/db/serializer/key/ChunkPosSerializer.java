package me.jellysquid.mods.radon.common.db.serializer.key;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemoryLayouts;
import jdk.incubator.foreign.MemorySegment;
import me.jellysquid.mods.radon.common.db.serializer.KeySerializer;
import me.jellysquid.mods.radon.common.natives.NativeUtil;
import net.minecraft.util.math.ChunkPos;

import java.lang.invoke.VarHandle;

public class ChunkPosSerializer implements KeySerializer<ChunkPos> {
    private static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
            MemoryLayouts.JAVA_INT.withName("x"),
            MemoryLayouts.JAVA_INT.withName("z")
    );
    private static final VarHandle X = LAYOUT.varHandle(int.class, MemoryLayout.PathElement.groupElement("x"));
    private static final VarHandle Z = LAYOUT.varHandle(int.class, MemoryLayout.PathElement.groupElement("z"));

    @Override
    public MemorySegment serializeKey(ChunkPos value) {
        var segment = NativeUtil.allocateNative(LAYOUT);

        X.set(segment, value.x);
        Z.set(segment, value.z);

        return segment;
    }
}