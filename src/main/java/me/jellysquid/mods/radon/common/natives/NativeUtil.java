package me.jellysquid.mods.radon.common.natives;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.LibraryLookup;
import jdk.incubator.foreign.MemoryLayout;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.stream.Collectors;

public class NativeUtil {
    protected static final LibraryLookup LibZSTD;

    protected static MethodHandle getHandle(LibraryLookup library, String method, Type returnType, Type... args) {
        var linker = CLinker.getInstance();
        var symbol = library.lookup(method);
        if (symbol.isEmpty()) {
            throw new NativeLibraryException("Couldn't find method "+method);
        }



        return linker.downcallHandle(
                symbol.get(),
                MethodType.methodType(returnType.javaRepresentation, Arrays.stream(args).map(t -> t.javaRepresentation).collect(Collectors.toList())),
                FunctionDescriptor.of(returnType.layout, Arrays.stream(args).map(t -> t.layout).toArray(MemoryLayout[]::new))
        );
    }


    static {
        LibZSTD = LibraryLookup.ofLibrary("zstd");
    }
}
