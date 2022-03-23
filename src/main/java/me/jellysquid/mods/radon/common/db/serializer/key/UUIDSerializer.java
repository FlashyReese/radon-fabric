package me.jellysquid.mods.radon.common.db.serializer.key;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemoryLayouts;
import jdk.incubator.foreign.MemorySegment;
import me.jellysquid.mods.radon.common.db.serializer.KeySerializer;
import me.jellysquid.mods.radon.common.natives.NativeUtil;

import java.lang.invoke.VarHandle;
import java.util.UUID;

public class UUIDSerializer implements KeySerializer<UUID> {
    private static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
            MemoryLayouts.JAVA_LONG.withName("least"),
            MemoryLayouts.JAVA_LONG.withName("most")
    );
    private static final VarHandle LEAST = LAYOUT.varHandle(long.class, MemoryLayout.PathElement.groupElement("least"));
    private static final VarHandle MOST = LAYOUT.varHandle(long.class, MemoryLayout.PathElement.groupElement("most"));

    @Override
    public MemorySegment serializeKey(UUID value) {
        var segment = NativeUtil.allocateNative(LAYOUT);

        LEAST.set(segment, value.getLeastSignificantBits());
        MOST.set(segment, value.getMostSignificantBits());

        return segment;
    }
}