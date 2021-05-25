package me.jellysquid.mods.radon.common.natives;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

public class CString {
    private final MemorySegment storage;

    public CString(String string) {
        MemorySegment segment = CLinker.toCString(string).share();
        this.storage = segment.registerCleaner(NativeUtil.CLEANER);
    }

    public MemoryAddress getPointer() {
        return storage.address();
    }
}
