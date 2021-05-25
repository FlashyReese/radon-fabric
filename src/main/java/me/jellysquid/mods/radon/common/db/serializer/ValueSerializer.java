package me.jellysquid.mods.radon.common.db.serializer;

import jdk.incubator.foreign.MemorySegment;

import java.io.IOException;

public interface ValueSerializer<T> {
    MemorySegment serialize(T value) throws IOException;

    T deserialize(MemorySegment input) throws IOException;
}
