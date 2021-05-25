package me.jellysquid.mods.radon.common.db.serializer.key;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemoryLayouts;
import jdk.incubator.foreign.MemorySegment;
import me.jellysquid.mods.radon.common.db.serializer.KeySerializer;
import me.jellysquid.mods.radon.common.natives.NativeUtil;
import net.minecraft.util.math.ChunkSectionPos;

import java.lang.invoke.VarHandle;

public class ChunkSectionPosSerializer implements KeySerializer<ChunkSectionPos> {
    private static final MemoryLayout LAYOUT = MemoryLayout.ofStruct(
            MemoryLayouts.JAVA_INT.withName("x"),
            MemoryLayouts.JAVA_INT.withName("y"),
            MemoryLayouts.JAVA_INT.withName("z")
    );
    private static final VarHandle X = LAYOUT.varHandle(int.class, MemoryLayout.PathElement.groupElement("x"));
    private static final VarHandle Y = LAYOUT.varHandle(int.class, MemoryLayout.PathElement.groupElement("y"));
    private static final VarHandle Z = LAYOUT.varHandle(int.class, MemoryLayout.PathElement.groupElement("z"));

    @Override
    public MemorySegment serializeKey(ChunkSectionPos value) {
        var segment = NativeUtil.allocateNative(LAYOUT);

        X.set(segment, value.getX());
        Y.set(segment, value.getY());
        Z.set(segment, value.getZ());

        return segment;
    }
}
