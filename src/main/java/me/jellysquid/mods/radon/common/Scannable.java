package me.jellysquid.mods.radon.common;


import jdk.incubator.foreign.MemorySegment;

import java.io.IOException;

public interface Scannable<T> {
    void scan(MemorySegment input, T scanner) throws IOException;
}