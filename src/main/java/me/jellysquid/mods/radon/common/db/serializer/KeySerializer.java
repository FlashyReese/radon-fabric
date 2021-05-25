package me.jellysquid.mods.radon.common.db.serializer;

import jdk.incubator.foreign.MemorySegment;

public interface KeySerializer<T> {
    MemorySegment serializeKey(T value);
}
