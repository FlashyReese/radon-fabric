package me.jellysquid.mods.radon.common.natives;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.LibraryLookup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public class NativeUtil {
    protected static final LibraryLookup LibZSTD;

    protected static MethodHandle getHandle(LibraryLookup library, String method, MethodType methodType, FunctionDescriptor functionDescriptor) {
        var linker = CLinker.getInstance();
        var symbol = library.lookup(method);
        if (symbol.isEmpty()) {
            throw new NativeLibraryException("Couldn't find method "+method);
        }

        return linker.downcallHandle(
                symbol.get(),
                methodType,
                functionDescriptor
        );
    }

    static {
        LibZSTD = LibraryLookup.ofLibrary("zstd");
    }
}
