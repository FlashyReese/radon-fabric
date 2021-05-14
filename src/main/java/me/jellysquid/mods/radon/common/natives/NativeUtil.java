package me.jellysquid.mods.radon.common.natives;

import jdk.incubator.foreign.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.ByteOrder;
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

    /**
     * Copies a C-style null terminated string into a java string.
     * This copies the chars so may not be the most efficient algorithm
     * @param pointer pointer to the char array
     * @return a Java {@link String}
     */
    protected static String pointerToString(MemoryAddress pointer) {
        var byteHandle = MemoryHandles.varHandle(byte.class, ByteOrder.nativeOrder());

        StringBuilder builder = new StringBuilder();
        long i = 0;
        while (true) {
            byte value = (byte)byteHandle.get(pointer.addOffset(i));
            if (value == 0) {
                break;
            }

            builder.append((char)value);
            i++;
        }

        return builder.toString();
    }


    static {
        LibZSTD = LibraryLookup.ofLibrary("zstd");
    }
}
