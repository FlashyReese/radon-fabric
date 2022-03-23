package me.jellysquid.mods.radon.common.natives;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public class CString {
    private final MemorySegment storage;

    public CString(String string) {
        this.storage = CLinker.toCString(string, ResourceScope.newSharedScope());
    }

    public MemoryAddress getPointer() {
        return storage.address();
    }
}