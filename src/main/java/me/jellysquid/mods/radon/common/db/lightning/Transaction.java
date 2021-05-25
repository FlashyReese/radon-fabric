package me.jellysquid.mods.radon.common.db.lightning;

import jdk.incubator.foreign.MemoryAddress;
import me.jellysquid.mods.radon.common.natives.NativeUtil;

@FunctionalInterface
interface Transaction<T> {
    T exec(NativeUtil.PointerBuf stack, MemoryAddress txn);

}