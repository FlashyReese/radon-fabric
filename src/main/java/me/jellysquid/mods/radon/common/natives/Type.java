package me.jellysquid.mods.radon.common.natives;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;

public enum Type {
    UINT(int.class, CLinker.C_INT),
    POINTER(MemoryAddress.class, CLinker.C_POINTER);

    public final Class<?> javaRepresentation;
    public final MemoryLayout layout;

    Type(Class<?> javaRepresentation, MemoryLayout layout) {
        this.javaRepresentation = javaRepresentation;
        this.layout = layout;
    }
}
